package services.game

import java.util.UUID

import models.game.GameStage
import models.node.{DoorNode, Node}
import models.options.GameOptions
import models.player.Player
import util.Point

object GameInstanceFactory {
  def create(options: GameOptions, initialNodes: Seq[Node], initialPlayers: Seq[Player], log: String => Unit, notify: String => Unit) = {
    val id = UUID.randomUUID

    val spawn = initialNodes.collectFirst { case n: DoorNode if n.name == "main" => n }.map { d =>
      Point(d.x.toInt + (d.width / 2), d.y.toInt + d.height - 24)
    }.getOrElse(throw new IllegalStateException("No spawn point detected."))

    val initialObjects = initialNodes.map(_.asNewGameObject)

    var lastNodeId = initialNodes.map(_.id).max + 1
    def nextNodeId() = {
      lastNodeId += 1
      lastNodeId
    }

    val playerObjects = initialPlayers.zipWithIndex.map(p => p._1.asNewGameObject(nextNodeId(), p._2, spawn))

    val finalObjects = (initialObjects ++ playerObjects).toIndexedSeq

    val gameStage = GameStage(options.map, finalObjects)

    val i = GameInstance(id, options, gameStage, spawn)
    i.setCallbacks(log, notify)
    i
  }
}
