package services.collision

import models.collision.CollisionPoly
import util.Rectangle

class PolygonMovement(max: (Double, Double), poly: CollisionPoly) extends MovementHelper {
  override def moveX(current: (Double, Double), bounds: Rectangle, deltaX: Double) = {
    val newX = current._1 + deltaX
    if (poly.check(newX, current._2)) {
      Math.max(0, Math.min(max._1, newX))
    } else {
      current._1
    }
  }

  override def moveY(current: (Double, Double), bounds: Rectangle, deltaY: Double) = {
    val newY = current._2 + deltaY
    if (poly.check(current._1, newY)) {
      Math.max(0, Math.min(max._2, newY))
    } else {
      current._2
    }
  }
}
