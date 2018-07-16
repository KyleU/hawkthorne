package models.node

import util.JsonSerializers._

object BreakableBlockNode {
  object Props {
    implicit val jsonEncoder: Encoder[Props] = deriveEncoder
    implicit val jsonDecoder: Decoder[Props] = deriveDecoder
  }

  case class Props(
      brokenBy: Option[String],
      crack: Option[String],
      dying_animation: Option[String],
      explode: Option[String],
      flipY: Option[String],
      flipped: Option[String],
      flippedY: Option[String],
      foreground: Option[String],
      hp: Option[String],
      sound: Option[String],
      sprite: Option[String],
      tile_id: Option[String]
  )

  val key = "breakable_block"
  implicit val jsonEncoder: Encoder[BreakableBlockNode] = deriveEncoder
  implicit val jsonDecoder: Decoder[BreakableBlockNode] = deriveDecoder
}

case class BreakableBlockNode(
    override val id: Int,
    override val name: String,
    override val x: Int,
    override val y: Int,
    override val width: Int,
    override val height: Int,
    override val rotation: Option[Int],
    override val visible: Option[Boolean],
    polygon: Option[Seq[Node.Point]],
    properties: BreakableBlockNode.Props
) extends Node(BreakableBlockNode.key)
