/* Generated File */
package models.data.character

import models.data.series.Episode
import models.player.Costume
import models.template.character.CharacterTemplate
import util.BoundingBox

object Duncan extends CharacterTemplate(
  key = "duncan",
  name = "Duncan",
  givenName = "Ian Duncan",

  costumes = Seq(
    Costume("base", Episode.S03E20, "Ian Duncan", 1)
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
