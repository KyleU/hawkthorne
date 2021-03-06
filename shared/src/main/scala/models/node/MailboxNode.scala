package models.node

import util.JsonSerializers._

object MailboxNode {
  val key = "mailbox"
  implicit val jsonEncoder: Encoder[MailboxNode] = deriveEncoder
  implicit val jsonDecoder: Decoder[MailboxNode] = deriveDecoder
}

final case class MailboxNode(
    override val id: Int,
    override val name: String,
    override val x: Int,
    override val y: Int,
    override val width: Int,
    override val height: Int,
    override val rotation: Int,
    override val visible: Boolean
) extends Node(MailboxNode.key)
