package controllers

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.mohiva.play.silhouette.api.HandlerResult
import io.circe.Json
import models.auth.Credentials
import models.{Application, RequestMessage, ResponseMessage}
import play.api.mvc.{AnyContent, AnyContentAsEmpty, Request, WebSocket}
import services.socket.SocketService
import util.web.{MessageFrameFormatter, WebsocketUtils}

import scala.concurrent.Future

@javax.inject.Singleton
class HomeController @javax.inject.Inject() (
    override val app: Application, implicit val system: ActorSystem, implicit val materializer: Materializer
) extends BaseController("home") {

  import app.contexts.webContext

  private[this] val formatter = new MessageFrameFormatter()

  def home() = withSession("home") { implicit request => implicit td =>
    Future.successful(Ok(views.html.index(request.identity, app.config.debug)))
  }

  def connect(binary: Boolean) = WebSocket.acceptOrResult[RequestMessage, ResponseMessage] { request =>
    implicit val req: Request[AnyContent] = Request(request, AnyContentAsEmpty)
    val connId = UUID.randomUUID()
    app.silhouette.SecuredRequestHandler { secured => Future.successful(HandlerResult(Ok, Some(secured.identity))) }.map {
      case HandlerResult(_, Some(user)) => Right(WebsocketUtils.actorRef(connId) { out =>
        SocketService.props(Some(connId), app.supervisor, Credentials(user, request.remoteAddress), out, request.remoteAddress)
      })
      case HandlerResult(_, None) => Left(Redirect(controllers.routes.HomeController.home()).flashing("error" -> "You're not signed in."))
    }
  }(formatter.transformer(binary))

  def externalLink(url: String) = withSession("external.link") { implicit request => implicit td =>
    Future.successful(Redirect(if (url.startsWith("http")) { url } else { "http://" + url }))
  }

  def ping(timestamp: Long) = withSession("ping") { implicit request => implicit td =>
    Future.successful(Ok(Json.obj("timestamp" -> Json.fromLong(timestamp))))
  }

  def robots() = withSession("robots") { implicit request => implicit td =>
    Future.successful(Ok("User-agent: *\nDisallow: /"))
  }
}
