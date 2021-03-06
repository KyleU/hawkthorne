package models.node

import util.JsonSerializers._

object CeilingNode {
  val key = "ceiling"
  implicit val jsonEncoder: Encoder[CeilingNode] = deriveEncoder
  implicit val jsonDecoder: Decoder[CeilingNode] = deriveDecoder
}

final case class CeilingNode(
    override val id: Int,
    override val name: String,
    override val x: Int,
    override val y: Int,
    override val width: Int,
    override val height: Int,
    override val rotation: Int,
    override val visible: Boolean
) extends Node(CeilingNode.key)
