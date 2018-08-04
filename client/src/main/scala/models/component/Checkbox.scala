package models.component

import com.definitelyscala.phaserce.{Game, Group, Image}

class Checkbox(override val game: Game, group: Group, override val name: String, initialX: Int, initialY: Int) extends SimpleComponent {
  val image = new Image(game = game, x = initialX.toDouble, y = initialY.toDouble, key = s"options.checkbox.$checked")
  image.tint = 0x000000
  image.name = s"checkbox.$name"
  group.add(image)

  override def comp = image

  private[this] var _checked = false
  def checked = _checked
  def checked_=(c: Boolean) = if (_checked != c) {
    _checked = c
    image.loadTexture(s"options.checkbox.$checked")
  }

  def toggle() = checked = !checked
}
