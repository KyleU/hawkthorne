/* Generated File */
package models.data.character

import models.data.series.Episode
import models.player.Costume
import models.template.character.CharacterTemplate
import util.BoundingBox

object Guzman extends CharacterTemplate(
  key = "guzman",
  name = "Guzman",
  givenName = "Luis Guzman",

  costumes = Seq(
    Costume("base", Episode.S03E20, "Luis Guzman", 1)
  ),

  boundingBox = BoundingBox(
    width = 14,
    height = 41,
    duckHeight = 20,
    x = 17,
    y = 7
  ),
  offset = 4
)
