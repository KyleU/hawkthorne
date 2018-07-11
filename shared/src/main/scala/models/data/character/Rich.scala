/* Generated File */
package models.data.character

import models.character.{BoundingBox, CharacterTemplate, Costume}
import models.data.series.Episode

object Rich extends CharacterTemplate(
  key = "rich",
  name = "Rich",
  givenName = "Dr. Rich Stephenson",

  costumes = Seq(
    Costume("base", Episode.S01E01, "Dr. Rich Stephenson", 1)
  ),

  boundingBox = BoundingBox(
    width = 14,
    height = 43,
    duckHeight = 20,
    x = 17,
    y = 5
  ),
  offset = 2
)
