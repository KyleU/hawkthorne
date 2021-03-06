/* Generated File */
package models.data.enemy

import models.animation.Animation
import models.template.enemy.EnemyTemplate

object BenzalkBoss extends EnemyTemplate(
  key = "benzalkBoss",
  name = "BenzalkBoss",
  width = 90,
  height = 90,
  hp = 100,
  damage = 40,
  isBoss = true,
  passiveSound = None,
  attackSounds = Nil,
  dieSound = None,
  sounds = Seq("benzalk_growl", "jump_boom"),
  animations = Seq(
    Animation(id = "jump.right", frames = IndexedSeq(9, 10, 11, 10), delay = 0.25, loop = true),
    Animation(id = "jump.left", frames = IndexedSeq(3, 4, 5, 4), delay = 0.25, loop = true),
    Animation(id = "attack.right", frames = IndexedSeq(12, 13, 14, 13, 12), delay = 0.2, loop = false),
    Animation(id = "attack.left", frames = IndexedSeq(2, 3, 4, 3, 2), delay = 0.2, loop = false),
    Animation(id = "default.right", frames = IndexedSeq(4, 5), delay = 0.25, loop = true),
    Animation(id = "default.left", frames = IndexedSeq(0, 1), delay = 0.25, loop = true),
    Animation(id = "hurt.right", frames = IndexedSeq(15), delay = 0.25, loop = true),
    Animation(id = "hurt.left", frames = IndexedSeq(7), delay = 0.25, loop = true),
    Animation(id = "dying.right", frames = IndexedSeq(25, 26, 27), delay = 0.25, loop = false),
    Animation(id = "dying.left", frames = IndexedSeq(11, 12, 13), delay = 0.25, loop = false)
  )
)
