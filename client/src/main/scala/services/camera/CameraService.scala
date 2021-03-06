package services.camera

import com.definitelyscala.phaserce.{Game, Group, Point, TilemapLayer}

import scala.scalajs.js

class CameraService(game: Game, group: Group, parallaxLayers: Seq[(TilemapLayer, Double)], desiredSize: (Int, Int), pxSize: (Int, Int)) {
  private[this] var zoom = 1.0
  private[this] val minZoom = 1.0
  private[this] val maxZoom = 10.0
  private[this] val size = Math.min(desiredSize._1, pxSize._1).toDouble -> Math.min(desiredSize._2, pxSize._2).toDouble
  private[this] var (currentX, currentY) = (0.0, 0.0)
  private[this] var (lastX, lastY) = 0.0 -> 0.0

  def currentZoom = zoom

  def resize(width: Int, height: Int) = {
    val desiredZ = Math.min(width / size._1, height / size._2)
    val newZ = Math.min(Math.max(desiredZ, minZoom), maxZoom)
    if (newZ != zoom) {
      zoom = newZ
      group.scale.set(zoom, zoom)
    }
    update()
  }

  def focusOn(x: Double, y: Double) = if (currentX != x || currentY != y) {
    currentX = x
    currentY = y
    update()
  }

  def worldToMap(x: Int, y: Int) = {
    val (nx, ny) = (x / currentZoom) -> (y / currentZoom)
    val (ox, oy) = (lastX / currentZoom) -> (lastY / currentZoom)
    // util.Logging.info(s"World [$x / $y] to map [$nx / $ny] (offset: [$ox / $oy])")
    (nx + ox).floor.toInt -> (ny + oy).floor.toInt
  }

  private[this] def update() = newCameraOffset().foreach { p =>
    parallaxLayers.foreach(l => l._1.asInstanceOf[js.Dynamic].tileOffset = new Point(0, 0))
    group.position = p
  }

  private[this] def newCameraOffset() = {
    val target = new Point((currentX * zoom) - (game.width / 2), (currentY * zoom) - (game.height / 2))

    val maxX = (pxSize._1 * zoom) - game.width // TODO Arrgh
    val maxY = (pxSize._2 * zoom) - game.height // TODO Arrgh

    val clampedX = Math.max(0.0, Math.min(maxX, target.x))
    val clampedY = Math.max(0.0, Math.min(maxY, target.y))
    if (clampedX != lastX || clampedY != lastY) {
      // util.Logging.info(s"zoom: [$zoom] clamped: [$clampedX, $clampedY] max: [$maxX, $maxY]")
      // util.Logging.info(s"game: [${game.width}, ${game.height}] group: [${group.width}, ${group.height}]")
      lastX = clampedX
      lastY = clampedY
      Some(new Point(-clampedX.toDouble, -clampedY.toDouble))
    } else {
      None
    }
  }
}
