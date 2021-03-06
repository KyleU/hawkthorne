package services.player

import java.util.UUID

import akka.actor.{Actor, ActorRef, Props, Timers}
import io.circe.Json
import models.InternalMessage.{ClientTraceRequest, _}
import models.RequestMessage._
import models.ResponseMessage._
import models.analytics.AnalyticsActionType
import models.auth.Credentials
import models.game.GameServiceMessage
import models.{Application, InternalMessage, RequestMessage, ResponseMessage}
import services.ServiceRegistry
import util.{Config, Version}

object ConnectionService {
  case class Callbacks(analytics: (AnalyticsActionType, Json) => Unit)

  def props(
    id: Option[UUID], connSupervisor: ActorRef, gameSupervisor: ActorRef,
    creds: Credentials, out: ActorRef, sourceAddr: String,
    app: Application, services: ServiceRegistry
  ) = {
    Props(new ConnectionService(id.getOrElse(UUID.randomUUID), connSupervisor, gameSupervisor, creds, out, sourceAddr, app, services))
  }
}

class ConnectionService(
    val id: UUID, connSupervisor: ActorRef, protected val gameSupervisor: ActorRef, protected val creds: Credentials,
    protected val out: ActorRef, protected val sourceAddr: String, protected val app: Application, protected val services: ServiceRegistry
) extends ConnectionServiceHelper with Actor with Timers {
  override def preStart() = {
    import util.JsonSerializers._
    log.info(s"Starting player connection for user [${creds.user.id}: ${creds.user.username}].")
    connSupervisor.tell(ConnectionStarted(creds, "player", id, self), self)
    out.tell(UserSettings(creds.user.id, creds.user.username, creds.user.profile.providerID), self)
    analytics(creds, AnalyticsActionType.Connect, Json.obj("source" -> sourceAddr.asJson, "version" -> Version.version.asJson))
  }

  override def receive = {
    // System
    case mr: MalformedRequest => log.error(s"MalformedRequest:  [${mr.reason}]: [${mr.content}].")
    case p: Ping => out.tell(Pong(p.ts, util.DateUtils.nowMillis), self)
    case _: GetVersion => out.tell(VersionResponse(Config.version), self)
    case dr: DebugRequest => onDebugRequest(dr)
    case sp: SetPlayer => setPlayer(sp.player)

    // Tracing
    case _: ConnectionTraceRequest => sender().tell(ConnectionTraceResponse(id = id, userId = creds.user.id, username = creds.user.username), self)
    case _: ClientTraceRequest => sendClientTrace()
    case ct: ClientTrace => returnClientTrace(ct.payload)

    // Analytics
    case am: AnalyticsMessage => analytics(creds, am.t, am.arg)

    // Game Service
    case sg: StartGame => startGame(sg.id, sg.options)
    case jg: JoinGame => joinGame(jg.id)
    case gj: GameServiceMessage.JoinedGame => onGameStarted(gj.actor, gj.gj)
    case gc: GameServiceMessage.CompletedGame => onGameComplete(gc.gf)

    // Game Requests
    case pu: PlayerUpdate => withGame("playerUpdate")((a, _) => a.tell(GameServiceMessage.Update(player.idx, pu), self))

    // Responses
    case rm: ResponseMessage => out.tell(rm, self)

    // Unhandled
    case im: InternalMessage => throw new IllegalArgumentException(s"Unhandled internal connection message [${im.getClass.getSimpleName}].")
    case rm: RequestMessage => throw new IllegalArgumentException(s"Unhandled connection request message [${rm.getClass.getSimpleName}].")
    case x => throw new IllegalArgumentException(s"Unhandled connection message with invalid class [${x.getClass.getSimpleName}].")
  }

  private[this] def onDebugRequest(dr: DebugRequest) = dr.t match {
    case "latency" => // TODO
    case _ => withGame("debugRequest")((a, g) => a.tell(GameServiceMessage.Debug(playerIdx = g.playerIdx, t = dr.t, payload = dr.payload), self))
  }

  override def postStop() = {
    activeGameOpt.foreach(g => g._1.tell(GameServiceMessage.Disconnect(g._2.playerIdx, "Server shutdown"), self))
    connSupervisor.tell(ConnectionStopped(id), self)
  }
}
