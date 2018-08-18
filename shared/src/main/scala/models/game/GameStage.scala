package models.game

import models.collision.{CollisionGrid, CollisionPoly}
import models.data.map.TiledMap
import models.game.obj.GameObject
import util.JsonSerializers._
import util.Rectangle

object GameStage {
  implicit val jsonEncoder: Encoder[GameStage] = deriveEncoder
  implicit val jsonDecoder: Decoder[GameStage] = deriveDecoder
}

final case class GameStage(sourceMap: TiledMap, var objects: IndexedSeq[GameObject]) {

  def collidingObjects(rect: Rectangle) = objects.collect { case o if o.loc.intersects(rect) => o }

  private[this] var collision: Option[Either[CollisionPoly, CollisionGrid]] = None

  def setCollision(coll: Either[CollisionPoly, CollisionGrid]) = collision = Some(coll)
  def getCollision = collision.getOrElse(throw new IllegalStateException("No collision configured."))

  def collidingBlocks(rect: Rectangle) = IndexedSeq.empty[(Int, Int)]
}
