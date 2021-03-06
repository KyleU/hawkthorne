package models.gui

import com.definitelyscala.phaserce.Easing.Easing
import com.definitelyscala.phaserce.{Game, Group, Image}
import models.component.SimpleComponent
import models.font.{Font, FontText}
import models.input.{MenuAction, PointerAction}
import services.audio.SoundEffectService

import scala.scalajs.js

final case class Menu(
    override val game: Game, override val name: String, fontKey: String, arrowKey: String, backgroundKey: String,
    width: Int, height: Int, margin: Double = 10.0, lineHeight: Double = 12.0, fontColor: Int = 0xffffff, fontOffset: Double = 0.0
) extends SimpleComponent {
  val group = new Group(game, name = name)
  override def comp = group

  private[this] var activeOption = -1
  private[this] var options = IndexedSeq.empty[(String, () => Unit)]
  private[this] var optionImages = Seq.empty[FontText]

  private[this] val font = Font.getFont(fontKey, game)

  val background = new Image(game = game, x = 0, y = 0, key = backgroundKey)
  background.name = s"menu.$name.background"
  group.add(background)

  private[this] val arrow = new Image(game, margin, margin, arrowKey)
  arrow.name = s"menu.$name.arrow"
  group.add(arrow)

  def optionCount = options.size

  def setOptions(opts: IndexedSeq[(String, () => Unit)]) = {
    optionImages.foreach { i =>
      group.remove(i.group)
      i.group.destroy()
    }
    options = opts
    optionImages = opts.zipWithIndex.map { opt =>
      val newX = (margin * 2) + arrow.width
      val newY = margin + (opt._2 * lineHeight) + fontOffset
      font.renderSimple(name = s"menu.$name.${opt._1._1}", text = opt._1._1, game = game, x = newX, y = newY, color = Some(fontColor))
    }
    optionImages.foreach(i => group.add(i.group))
    setActiveOption(idx = 0, playSound = false)
  }

  private[this] def previousItem() = activeOption match {
    case _ if activeOption == 0 => // noop
    case _ => setActiveOption(activeOption - 1)
  }

  private[this] def nextItem() = activeOption match {
    case _ if activeOption == options.length - 1 => // noop
    case _ => setActiveOption(activeOption + 1)
  }

  def setActiveOption(idx: Int, playSound: Boolean = true) = {
    if (playSound) { SoundEffectService.play("click") }
    activeOption = idx
    val props = js.Dynamic.literal(y = margin + (idx * lineHeight))
    game.add.tween(arrow).to(properties = props, duration = 100, ease = Easing.Default, autoStart = true, delay = 0, repeat = 0, yoyo = false)
  }

  def onSelect() = {
    SoundEffectService.play("confirm")
    options(activeOption)._2()
  }

  def onMenuAction(act: MenuAction) = act match {
    case MenuAction.Up => previousItem()
    case MenuAction.Down => nextItem()
    case MenuAction.Select => onSelect()
    case _ => // noop
  }

  def onPointer(act: PointerAction, zoom: Double, scaleMultiplier: Int = 1) = {
    val (x, y) = ((act.worldX - group.worldPosition.x) / zoom, (act.worldY - group.worldPosition.y) / zoom)
    if (x >= 0 && x <= width && y >= 0 && y <= height) {
      val idx = ((y + margin) / (lineHeight * scaleMultiplier)).toInt - 1
      if (idx >= 0 && idx < optionCount) {
        setActiveOption(idx = idx, playSound = false)
        onSelect()
      }
    }
  }

  override def destroy() = group.destroy()
}
