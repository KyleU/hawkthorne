/* Generated File */
package models.data.npc

import models.animation.Animation
import models.template.npc.NpcTemplate

object Frankie extends NpcTemplate(
  key = "frankie",
  name = "Frankie",
  width = 24,
  height = 48,
  greeting = Some("I am a big believer in hierarchy. Someone needs to say that I am in charge, and that person is me."),
  noInventory = None,
  noCommands = None,
  animations = Seq(
    Animation(id = "default", frames = IndexedSeq(0, 0, 0, 1), delay = 0.25, loop = true)
  )
)
