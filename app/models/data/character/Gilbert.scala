/* Generated File */
package models.data.character

import models.character.{BoundingBox, CharacterTemplate, Costume}

object Gilbert {
  val name = "Gilbert"
  val givenName = "Gilbert Lawson"

  val costumes = Seq(
    Costume("base", "base", "Gilbert Lawson", 1)
  )

  val boundingBox = BoundingBox(
    width = 14,
    height = 38,
    duckHeight = 20,
    x = 17,
    y = 10
  )
  val offset = 7

  val template = CharacterTemplate("gilbert", name, givenName, costumes, boundingBox, offset)
}
