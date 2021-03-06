package models.node

import models.asset.Asset
import models.game.obj.Enemy
import models.template.enemy.EnemyListing
import util.JsonSerializers._

object EnemyNode {
  object Props {
    implicit val jsonEncoder: Encoder[Props] = deriveEncoder
    implicit val jsonDecoder: Decoder[Props] = deriveDecoder
  }

  final case class Props(
      direction: Option[String],
      displacement: Option[String],
      drop: Option[String],
      enemytype: Option[String],
      quest: Option[String],
      sheet: Option[String]
  )

  val key = "enemy"
  implicit val jsonEncoder: Encoder[EnemyNode] = deriveEncoder
  implicit val jsonDecoder: Decoder[EnemyNode] = deriveDecoder
}

final case class EnemyNode(
    override val id: Int,
    override val name: String,
    override val x: Int,
    override val y: Int,
    override val width: Int,
    override val height: Int,
    override val rotation: Int,
    override val visible: Boolean,
    properties: EnemyNode.Props
) extends Node(EnemyNode.key) {

  override val actualName = if (name.isEmpty) {
    properties.enemytype.getOrElse(throw new IllegalStateException(s"No enemy name for [$id]."))
  } else {
    name
  }

  val sheet = properties.sheet.getOrElse(actualName)

  val template = EnemyListing.withKey(actualName)

  override val assets = Seq(Asset.Spritesheet(s"$t.$sheet", s"images/enemies/$sheet.png", template.width, template.height)) ++
    template.sounds.map(Asset.sfx) ++
    template.passiveSound.map(Asset.sfx) ++
    template.attackSounds.map(Asset.sfx) ++
    template.dieSound.map(Asset.sfx)

  override def asNewGameObject = Seq(Enemy(id = id, n = actualName, loc = asLocation, vis = visible))
}
