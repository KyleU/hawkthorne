package models.modal

import com.definitelyscala.phaserce._
import models.asset.Asset
import models.component.{BaseComponent, SimpleComponent}
import services.audio.SoundEffectService

object BaseModal {
  val assets = Seq(Asset.Image("modal.background", "images/custom/dialog.png"), Asset.sfx("menu_expand"), Asset.sfx("menu_close"))
  val (width, height) = (312, 60)
  val speed = 2
}

abstract class BaseModal(override val game: Game, val name: String) extends SimpleComponent with BaseComponent.Resizable {
  private[this] var openCallback: Option[() => Unit] = None
  private[this] var closeCallback: Option[() => Unit] = None

  private[this] var targetAlpha = 0.0
  private[this] var (opening, closing) = (false, false)

  private[this] val group = new Group(game = game, name = s"modal.$name.group", addToStage = true)
  group.scale.set(3, 3)
  group.alpha = 0.0
  group.visible = false

  override def comp = group

  private[this] val sprite = new Sprite(game = game, x = 0, y = 0, key = "modal.background")
  sprite.name = s"modal.$name.background"

  group.add(sprite)
  resize(game.width.toInt, game.height.toInt)
  def open(onOpen: () => Unit) = {
    SoundEffectService.play(key = "menu_expand", vol = 0.5)
    group.visible = true
    opening = true
    closing = false
    targetAlpha = 1.0
    openCallback.foreach(_ => throw new IllegalStateException("Previous open has not been cleared."))
    openCallback = Some(onOpen)
    update(0)
    visible = true
  }

  def close(onClose: () => Unit) = {
    SoundEffectService.play(key = "menu_close", vol = 0.5)
    opening = false
    closing = true
    targetAlpha = 0.0
    closeCallback.foreach(_ => throw new IllegalStateException("Previous close has not been cleared."))
    closeCallback = Some(onClose)
    update(0)
  }

  override def update(deltaMs: Double) = if (group.visible) {
    if (group.alpha == targetAlpha) {
      if (opening) {
        opening = false
        openCallback.foreach(_())
        openCallback = None
      }
      if (closing) {
        closing = false
        closeCallback.foreach(_())
        closeCallback = None
        group.visible = false
      }
    } else {
      val delta = deltaMs * BaseModal.speed.toDouble
      if (opening) {
        group.alpha = Math.min(targetAlpha, group.alpha + delta)
      }
      if (closing) {
        group.alpha = Math.max(targetAlpha, group.alpha - delta)
      }
    }
  }

  override def resize(width: Int, height: Int) = {
    val fullWidth = sprite.width * group.scale.x
    val fullHeight = sprite.height * group.scale.y
    group.position.set((width - fullWidth) / 2.0, (height - fullHeight) / 2.0)
  }

  override def destroy() = group.destroy()
}