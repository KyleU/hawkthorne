package models.game

import models.collision.{CollisionGrid, CollisionPoly}
import models.data.map.TiledMap
import util.JsonSerializers._

object GameStage {
  implicit val jsonEncoder: Encoder[GameStage] = deriveEncoder
  implicit val jsonDecoder: Decoder[GameStage] = deriveDecoder
}

final case class GameStage(sourceMap: TiledMap, var objects: IndexedSeq[GameObject]) {

  def collidingObjects(x: Double, y: Double) = objects.collect {
    case o if o.x < x && o.y < y && (o.x + o.w) >= x && (o.y + o.h) >= y => o
  }

  private[this] var collision: Option[Either[CollisionPoly, CollisionGrid]] = None

  def setCollision(coll: Either[CollisionPoly, CollisionGrid]) = collision = Some(coll)
  def getCollision = collision.getOrElse(throw new IllegalStateException("No collision configured."))
}
