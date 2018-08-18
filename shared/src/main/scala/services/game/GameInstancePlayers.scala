package services.game

import java.util.UUID

import models.game.GameMessage
import models.input.PlayerInputHandler
import models.player.{Player, PlayerRecord}
import services.game.GameInstanceDebug.{debug, log}

trait GameInstancePlayers { this: GameInstance =>
  protected[this] var players = IndexedSeq.empty[PlayerRecord]
  protected[this] def indexOfPlayerId(id: UUID) = players.indexWhere(_.player.id == id)

  protected[this] def addPlayer(player: Player) = {
    val playerIndex = indexOfPlayerId(player.id) match {
      case -1 =>
        val idx = players.size
        val pih = new PlayerInputHandler(
          instance = this, playerIdx = idx, boundingBox = player.template.boundingBox, initialX = spawn.x, initialY = spawn.y, log = log
        )
        players = players :+ PlayerRecord(idx = idx, player = player, input = pih)
        idx
      case x =>
        players = players.zipWithIndex.map { record =>
          if (record._1.idx == x) {
            // TODO Notify replaced player?
            record._1.copy(player = player)
          } else {
            record._1
          }
        }
        x
    }

    debug(s"Added player [$player] to game, making [${players.size}] total players ([${players.count(_.player.attributes.connected)}] active).")
    GameMessage.PlayerAdded(playerIndex, player)
  }

  protected[this] def removePlayer(id: UUID) = {
    players.collectFirst {
      case x if x.player.id == id => x.player.attributes.connected = false
    }
    debug(s"Removed player [$id] from game, making [${players.size}] total players ([${players.count(_.player.attributes.connected)}] active).")
    GameMessage.PlayerRemoved(players.size - 1, id)
  }
}
