package services.options

import com.definitelyscala.phaserce.Game
import models.asset.Asset
import models.component.VerticalParticles
import models.font.Font
import models.input.MenuAction
import services.audio.MusicService
import services.input.InputService
import services.state.{GameState, LoadingState}

object OptionsState {
  def load(phaser: Game, inputService: InputService, debug: Boolean) = new LoadingState(
    next = new OptionsState(phaser = phaser, inputService = inputService, debug = debug), phaser = phaser,
    assets = Font.assets :+ Asset.music("daybreak") :+ Asset.Image("options.menu.bg", "images/menu/pause.png")
  )
}

class OptionsState(phaser: Game, inputService: InputService, debug: Boolean) extends GameState("test", phaser) {
  private[this] var particles: Option[VerticalParticles] = None
  private[this] var options: Option[OptionsMenu] = None

  override def create(game: Game) = {
    Font.reset()
    MusicService.play("daybreak", loop = true)
    particles = Some(new VerticalParticles(game))
    options = Some(new OptionsMenu(game))
    inputService.menuHandler.setCallback(Some(x => onMenuAction(x)))
    resize(game.width, game.height)
  }

  override def update(game: Game) = {
    val dt = game.time.physicsElapsed
    particles.foreach(_.update(dt))
    options.foreach(_.update(dt))
    inputService.update(dt)
  }

  override def shutdown(game: Game) = {
    MusicService.stop("daybreak")
    inputService.menuHandler.setCallback(None)
    super.shutdown(game)
  }

  override def onResize(width: Int, height: Int) = {
    options.foreach(_.resize(width, height))
  }

  private[this] def onMenuAction(acts: Seq[MenuAction]) = acts.foreach { act =>
    util.Logging.info(s"Options menu action: $act")
  }
}
