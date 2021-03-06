package models.component

import com.definitelyscala.phaserce.{Game, Group, Image}
import models.options.SystemOptions

final case class Liquid(
    override val game: Game,
    group: Group,
    override val name: String,
    key: String,
    opacity: Double,
    speed: Double,
    width: Int,
    height: Int
) extends SimpleComponent {
  private[this] var currFrameTime = 0.0

  val liquidGroup = new Group(game, group, name)
  override def comp = liquidGroup
  liquidGroup.name = s"liquid.$name"

  val frameCount = game.cache.getFrameCount(key).toInt / 2

  val images = (0 until height).flatMap { yIdx =>
    (0 until width).map { xIdx =>
      // val nf = if (yIdx == 0) { Random.nextInt(frameCount) } else { Random.nextInt(frameCount) + frameCount }
      val nf = if (yIdx == 0) { xIdx % frameCount } else { ((xIdx + yIdx) % frameCount) + frameCount }

      val image = new Image(game = game, x = xIdx.toDouble * SystemOptions.tileSize, y = yIdx.toDouble * SystemOptions.tileSize, key = key, frame = nf)
      image.name = s"liquid.$name.$xIdx.$yIdx"
      image.alpha = opacity
      liquidGroup.add(image)
      image
    }
  }

  override def update(deltaMs: Double) = {
    currFrameTime += deltaMs
    if (currFrameTime > speed) {
      currFrameTime = currFrameTime % speed
      images.foreach { i =>
        val currFrame = i.frame.asInstanceOf[Double].toInt
        val buffer = if (currFrame >= frameCount) { frameCount } else { 0 }
        i.frame = ((currFrame + 1) % frameCount) + buffer
      }
    }
  }

  override def destroy() = liquidGroup.destroy()
}
