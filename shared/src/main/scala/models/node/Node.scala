package models.node

import models.asset.Asset
import util.JsonSerializers._

object Node {
  object Point {
    implicit val jsonEncoder: Encoder[Point] = deriveEncoder
    implicit val jsonDecoder: Decoder[Point] = deriveDecoder
  }

  case class Point(x: Int, y: Int)

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
  def rotation: Option[Int]
  def visible: Option[Boolean]

  def assets = Seq.empty[Asset]
  def update(deltaMs: Double) = {}
}
