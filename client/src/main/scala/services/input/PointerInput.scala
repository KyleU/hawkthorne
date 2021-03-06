package services.input

import com.definitelyscala.phaserce.Game
import models.input.PointerAction

final case class PointerInput(game: Game) {
  game.input.mouse.capture = true

  private[this] var leftMouseWasDown = false

  def update(delta: Double) = {
    val ptr = game.input.activePointer
    if (ptr.leftButton.isDown) {
      if (!leftMouseWasDown) {
        leftMouseWasDown = true
        // util.Logging.info(s"Button pressed [World: ${ptr.worldX}/${ptr.worldY}] [Screen: ${ptr.screenX}/${ptr.screenY}]")
        Some(PointerAction(ptr.screenX.floor.toInt, ptr.screenY.floor.toInt, ptr.worldX.floor.toInt, ptr.worldY.floor.toInt))
      } else {
        None
      }
    } else {
      leftMouseWasDown = false
      None
    }
  }

  def close() = {}
}
