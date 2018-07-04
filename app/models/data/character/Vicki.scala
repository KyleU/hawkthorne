/* Generated File */
package models.data.character

import models.character.{BoundingBox, CharacterTemplate, Costume}

object Vicki {
  val name = "Vicki"
  val givenName = "Vicki Cooper"

  val costumes = Seq(
    Costume("base", "base", "Vicki Cooper", 1),
    Costume("knight", "s3e19", "Chess Knight", 2)
  )

  val boundingBox = BoundingBox(
    width = 14,
    height = 35,
    duckHeight = 20,
    x = 17,
    y = 13
  )
  val offset = 7

  val template = CharacterTemplate("vicki", name, givenName, costumes, boundingBox, offset)
}
