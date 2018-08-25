package services.game

import java.util.UUID

import models.collision.{CollisionGrid, CollisionPoly}
import models.data.map.TiledMap
import models.game.obj.GameObject
import models.options.SystemOptions
import util.{IntPoint, Rectangle}

object GameMap {
  import util.JsonSerializers._

  implicit val jsonEncoder: Encoder[GameMap] = deriveEncoder
  implicit val jsonDecoder: Decoder[GameMap] = deriveDecoder
}

final case class GameMap(gameId: UUID, map: TiledMap, var objects: IndexedSeq[GameObject]) {
  private[this] var spawns = Map.empty[String, IntPoint]
  private[this] var collision: Option[Either[CollisionPoly, CollisionGrid]] = None

  val bounds = (map.width * SystemOptions.tileSize) -> (map.height * SystemOptions.tileSize)

  def setSpawns(m: Map[String, IntPoint]) = spawns = m
  def spawnPoints(p: String) = spawns.getOrElse(p, {
    throw new IllegalStateException(s"No spawn point [$p] for map [$map] among [${spawns.values.mkString(", ")}]")
  })

  def collidingObjects(rect: Rectangle) = objects.collect { case o if o.loc.intersects(rect) => o }

  def setCollision(coll: Either[CollisionPoly, CollisionGrid]) = collision = Some(coll)
  def getCollision = collision.getOrElse(throw new IllegalStateException("No collision configured."))

  def collidingBlocks(rect: Rectangle) = IndexedSeq.empty[(Int, Int)]
}