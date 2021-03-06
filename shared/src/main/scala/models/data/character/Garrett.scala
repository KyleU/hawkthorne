/* Generated File */
package models.data.character

import models.data.series.Episode
import models.player.Costume
import models.template.character.CharacterTemplate
import util.BoundingBox

object Garrett extends CharacterTemplate(
  key = "garrett",
  name = "Garrett",
  givenName = "Garrett Lambert",

  costumes = Seq(
    Costume("base", Episode.S03E20, "Garrett Lambert", 1),
    Costume("jammies", Episode.S03E13, "Camo Jammies", 2),
    Costume("alien", Episode.S01E05, "Creepy Alien", 3)
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
