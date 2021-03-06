package models.node

import util.JsonSerializers._

object BouncerNode {
  object Props {
    implicit val jsonEncoder: Encoder[Props] = deriveEncoder
    implicit val jsonDecoder: Decoder[Props] = deriveDecoder
  }

  final case class Props(bval: Option[String], dbval: String)

  val key = "bouncer"
  implicit val jsonEncoder: Encoder[BouncerNode] = deriveEncoder
  implicit val jsonDecoder: Decoder[BouncerNode] = deriveDecoder
}

final case class BouncerNode(
    override val id: Int,
    override val name: String,
    override val x: Int,
    override val y: Int,
    override val width: Int,
    override val height: Int,
    override val rotation: Int,
    override val visible: Boolean,
    properties: BouncerNode.Props
) extends Node(BouncerNode.key)
