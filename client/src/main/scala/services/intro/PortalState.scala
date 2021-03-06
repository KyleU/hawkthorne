package services.intro

import com.definitelyscala.phaserce.Game
import models.component.Portal
import models.data.character.Abed
import models.font.Font
import models.settings.ActivePlayers
import services.input.InputService
import services.state.{GameState, LoadingState}

object PortalState {
  val tempCostume = Abed.randomCostume

  def load(phaser: Game, inputService: InputService, debug: Boolean) = new LoadingState(
    next = new PortalState(phaser = phaser, inputService = inputService, debug = debug), phaser = phaser,
    assets = Portal.assets("abed", tempCostume.key)
  )
}

class PortalState(phaser: Game, inputService: InputService, debug: Boolean) extends GameState("test", phaser) {
  private[this] var portal: Option[Portal] = None

  override def create(game: Game) = {
    Font.reset()
    portal = Some(new Portal(game, ActivePlayers.getPlayers.head))
    inputService.menuHandler.setCallback(Some(_ => ()))
  }

  override def update(game: Game) = {
    val dt = game.time.physicsElapsed
    portal.foreach(_.update(dt))
    inputService.update(dt)
  }

  override def shutdown(game: Game) = {
    inputService.menuHandler.setCallback(None)
    super.shutdown(game)
  }
}
