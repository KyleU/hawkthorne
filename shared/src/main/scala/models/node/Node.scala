package models.node

import util.JsonSerializers._

object Node {
  implicit val jsonEncoder: Encoder[Node] = NodeEncoder.jsonEncoder
  implicit val jsonDecoder: Decoder[Node] = NodeDecoder.jsonDecoder
}

abstract class Node(val t: String) {
  def id: Int
  def name: String
  def x: Int
  def y: Int
  def width: Int
  def height: Int
}
