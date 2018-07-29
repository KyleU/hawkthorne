package services.input

import com.definitelyscala.phaserce.{Game, Key, KeyCode}
import models.input.InputUpdate
import services.debug.DebugService
import util.PhaserUtils

object KeyboardInput {
  final case class Keymap(
      up: Key, down: Key, left: Key, right: Key,
      jump: Key, attack: Key,
      select: Key, confirm: Key,
      options: Key, debug: Key
  ) {
    override def toString = s"Up: ${up.isDown}, Down: ${down.isDown}, Left: ${left.isDown}, Right: ${right.isDown}, " +
      s"Jump: ${jump.isDown}, Attack: ${attack.isDown}, " +
      s"Select: ${select.isDown}, Confirm: ${confirm.isDown}, " +
      s"Options: ${options.isDown}, Debug: ${debug.isDown}"
  }
}

final case class KeyboardInput(game: Game) {
  val keymap = KeyboardInput.Keymap(
    up = game.input.keyboard.addKey(KeyCode.UP),
    down = game.input.keyboard.addKey(KeyCode.DOWN),
    left = game.input.keyboard.addKey(KeyCode.LEFT),
    right = game.input.keyboard.addKey(KeyCode.RIGHT),

    jump = game.input.keyboard.addKey(KeyCode.SPACEBAR),
    attack = game.input.keyboard.addKey(KeyCode.CONTROL),

    select = game.input.keyboard.addKey(KeyCode.ALT),
    confirm = game.input.keyboard.addKey(KeyCode.ENTER),

    options = game.input.keyboard.addKey(KeyCode.ESC),
    debug = game.input.keyboard.addKey(KeyCode.QUESTION_MARK)
  )

  PhaserUtils.addToSignal(keymap.debug.onDown, _ => DebugService.inst.foreach(_.toggle()))

  def update(delta: Double) = {
    val x = if (keymap.left.isDown) { -1.0 } else if (keymap.right.isDown) { 1.0 } else { 0.0 }
    val y = if (keymap.up.isDown) { -1.0 } else if (keymap.down.isDown) { 1.0 } else { 0.0 }
    val commands = Seq(
      if (keymap.jump.justDown) { Some("jump") } else { None },
      if (keymap.attack.justDown) { Some("attack") } else { None },

      if (keymap.select.justDown) { Some("select") } else { None },
      if (keymap.confirm.justDown) { Some("confirm") } else { None },

      if (keymap.options.justDown) { Some("options") } else { None },
      if (keymap.debug.justDown) { Some("debug") } else { None }
    ).flatten
    InputUpdate(0, x, y, commands)
  }

  def close() = {}
}
