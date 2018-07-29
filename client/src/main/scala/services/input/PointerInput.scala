package services.input

import com.definitelyscala.phaserce.Game

final case class PointerInput(game: Game) {
  game.input.mouse.capture = true

  def update(delta: Double) = {
    val ptr = game.input.activePointer
    if (ptr.leftButton.isDown) {
      util.Logging.info(s"Button pressed [World: ${ptr.worldX}/${ptr.worldY}] [Screen: ${ptr.screenX}/${ptr.screenY}]")
    }
  }

  def close() = {}
}
