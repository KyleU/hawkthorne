/* Generated File */
package models.data.weapon

import models.animation.Animation
import models.template.weapon.WeaponTemplate

object Switch extends WeaponTemplate(
  key = "switch",
  name = "Switch",
  width = 50,
  height = 40,
  hitAudioClip = Some("punch"),
  swingAudioClip = Some("sword_air"),
  unuseAudioClip = Some("sword_sheathed"),
  animations = Seq(
    Animation(id = "default", frames = IndexedSeq(0), delay = 1.0, loop = false),
    Animation(id = "wield", frames = IndexedSeq(0, 1, 2), delay = 0.11, loop = false)
  )
)
