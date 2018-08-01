package services.state

import com.definitelyscala.phaserce.{Game, Sound}
import models.asset.Asset
import models.component.VerticalParticles
import models.font.Font
import models.input.MenuAction
import services.audio.MusicService
import services.input.InputService

object OptionsState {
  def load(phaser: Game, inputService: InputService, debug: Boolean) = new LoadingState(
    next = new OptionsState(phaser = phaser, inputService = inputService, debug = debug), phaser = phaser,
    assets = Font.assets :+ Asset.music("daybreak")
  )
}

class OptionsState(phaser: Game, inputService: InputService, debug: Boolean) extends GameState("test", phaser) {
  private[this] var particles: Option[VerticalParticles] = None

  override def create(game: Game) = {
    Font.reset()
    MusicService.play("daybreak", loop = true)
    particles = Some(new VerticalParticles(game))
    inputService.menuHandler.setCallback(Some(x => onMenuAction(x)))
  }

  override def update(game: Game) = {
    val dt = game.time.physicsElapsed
    particles.foreach(_.update(dt))
    inputService.update(dt)
  }

  override def shutdown(game: Game) = {
    MusicService.stop("daybreak")
    inputService.menuHandler.setCallback(None)
    super.shutdown(game)
  }

  private[this] def onMenuAction(acts: Seq[MenuAction]) = acts.foreach { act =>
    util.Logging.info(s"Options menu action: $act")
  }
}
