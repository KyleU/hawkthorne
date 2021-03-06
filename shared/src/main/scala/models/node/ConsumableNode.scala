package models.node

import models.asset.Asset
import models.game.obj.Consumable
import util.JsonSerializers._

object ConsumableNode {
  val key = "consumable"
  implicit val jsonEncoder: Encoder[ConsumableNode] = deriveEncoder
  implicit val jsonDecoder: Decoder[ConsumableNode] = deriveDecoder
}

final case class ConsumableNode(
    override val id: Int,
    override val name: String,
    override val x: Int,
    override val y: Int,
    override val width: Int,
    override val height: Int,
    override val rotation: Int,
    override val visible: Boolean
) extends Node(ConsumableNode.key) {
  override val assets = Seq(Asset.Image(s"$t.$actualName", s"images/consumables/$actualName.png"))
  override def asNewGameObject = Seq(Consumable(id = id, n = actualName, loc = asLocation, vis = visible))
}
