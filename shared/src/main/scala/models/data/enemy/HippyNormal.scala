/* Generated File */
package models.data.enemy

import models.animation.Animation
import models.template.enemy.EnemyTemplate

object HippyNormal extends EnemyTemplate(
  key = "hippy-normal",
  name = "HippyNormal",
  width = 48,
  height = 48,
  hp = 6,
  damage = 10,
  isBoss = false,
  passiveSound = None,
  attackSounds = Seq("peace", "sex", "drugs"),
  dieSound = Some("hippy_kill"),
  sounds = Seq.empty,
  animations = Seq(
    Animation(id = "dying.right", frames = IndexedSeq(11), delay = 1.0, loop = false),
    Animation(id = "dying.left", frames = IndexedSeq(5), delay = 1.0, loop = false),
    Animation(id = "default.right", frames = IndexedSeq(6, 7), delay = 0.25, loop = true),
    Animation(id = "default.left", frames = IndexedSeq(2, 3), delay = 0.25, loop = true),
    Animation(id = "hurt.right", frames = IndexedSeq(9), delay = 0.25, loop = true),
    Animation(id = "hurt.left", frames = IndexedSeq(4), delay = 0.25, loop = true),
    Animation(id = "attack.right", frames = IndexedSeq(2, 3), delay = 0.25, loop = true),
    Animation(id = "attack.left", frames = IndexedSeq(0, 1), delay = 0.25, loop = true)
  )
)
