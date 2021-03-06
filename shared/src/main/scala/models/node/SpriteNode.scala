package models.node

import models.animation.Animation
import models.asset.Asset
import util.JsonSerializers._

import scala.util.Random

object SpriteNode {
  object Props {
    implicit val jsonEncoder: Encoder[Props] = deriveEncoder
    implicit val jsonDecoder: Decoder[Props] = deriveDecoder
  }

  final case class Props(
      animation: Option[String],
      depth: Option[String],
      direction: Option[String],
      flip: Option[String],
      foreground: Option[String],
      height: Option[String],
      jumanji: Option[String],
      max_x: Option[String],
      min_x: Option[String],
      mode: Option[String],
      moveable_x: Option[String],
      offsetY: Option[String],
      random: Option[String],
      sheet: String,
      speed: Option[String],
      velocity_x: Option[String],
      width: Option[String],
      window: Option[String]
  )

  val key = "sprite"
  implicit val jsonEncoder: Encoder[SpriteNode] = deriveEncoder
  implicit val jsonDecoder: Decoder[SpriteNode] = deriveDecoder
}

final case class SpriteNode(
    override val id: Int,
    override val name: String,
    override val x: Int,
    override val y: Int,
    override val width: Int,
    override val height: Int,
    override val rotation: Int,
    override val visible: Boolean,
    properties: SpriteNode.Props
) extends Node(SpriteNode.key) {
  val sheetKey = "sprite." + properties.sheet.substring(properties.sheet.lastIndexOf('/') + 1).stripSuffix(".png")
  val animation = properties.animation.map { a =>
    Animation.fromString(id = actualName, s = a, speed = properties.speed.map(_.toDouble).getOrElse(0.1), loop = true)
  }
  if (properties.random.contains("true")) { animation.foreach(_.setJitter(Random.nextDouble())) }

  override def actualName = if (name.trim.isEmpty) { s"$sheetKey.$id" } else { name }
  override val actualWidth = properties.width.map(_.toInt).getOrElse(width)
  override val actualHeight = properties.height.map(_.toInt).getOrElse(height)

  override lazy val assets = Seq(Asset.Spritesheet(sheetKey, properties.sheet, actualWidth, actualHeight))
}
