package models.component

import com.definitelyscala.phaserce._
import models.font.Font

final case class ConsoleLog(override val game: Game) extends BaseComponent with BaseComponent.Resizable {
  override val name = "ui.console"

  val group = new Group(game, name = "console.log")
  group.x = 120
  group.y = 10

  val font = Font.getFont("arial", game)

  val image = font.renderToImage("test.text", "This is a test of the emergency broadcast system. This is only a test!", game)
  group.add(image)

  game.stage.add(group)

  override def resize(width: Int, height: Int) = {
    //group.scale = new Point(2.0, 2.0)
  }

  override def x = group.x
  override def x_=(newX: Double) = group.x = newX
  override def y = group.y
  override def y_=(newY: Double) = group.y = newY

  override def visible = group.visible
  override def visible_=(v: Boolean) = group.visible = v
}

