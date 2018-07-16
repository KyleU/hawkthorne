package models.node

import util.JsonSerializers._

object ConsumableNode {
  val key = "consumable"
  implicit val jsonEncoder: Encoder[ConsumableNode] = deriveEncoder
  implicit val jsonDecoder: Decoder[ConsumableNode] = deriveDecoder
}

case class ConsumableNode(
    override val id: Int,
    override val name: String,
    override val x: Int,
    override val y: Int,
    override val width: Int,
    override val height: Int,
    override val rotation: Option[Int],
    override val visible: Option[Boolean]
) extends Node(ConsumableNode.key)
