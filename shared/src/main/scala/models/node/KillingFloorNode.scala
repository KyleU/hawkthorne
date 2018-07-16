package models.node

import util.JsonSerializers._

object KillingFloorNode {
  val key = "killing_floor"
  implicit val jsonEncoder: Encoder[KillingFloorNode] = deriveEncoder
  implicit val jsonDecoder: Decoder[KillingFloorNode] = deriveDecoder
}

case class KillingFloorNode(
    override val id: Int,
    override val name: String,
    override val x: Int,
    override val y: Int,
    override val width: Int,
    override val height: Int,
    override val rotation: Option[Int],
    override val visible: Option[Boolean]
) extends Node(KillingFloorNode.key)
