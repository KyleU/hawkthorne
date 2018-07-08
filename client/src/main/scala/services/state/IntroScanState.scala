package services.state

import com.definitelyscala.phaserce._
import models.animation.Animation
import models.phaser.AnimatedSprite

object IntroScanState {
  def load(phaser: Game) = {
    new LoadingState(
      next = new IntroScanState(phaser),
      phaser = phaser,
      audio = Seq("music.opening" -> s"audio/music/opening.ogg"),
      spritesheets = Seq(
        ("intro.backgrounds", "images/intro/backgrounds.png", 400, 250),
        ("intro.names", "images/intro/names.png", 75, 15),
        ("intro.computer", "images/intro/computer.png", 75, 19),
        ("intro.description", "images/intro/description.png", 121, 13),
        ("intro.scanningbar", "images/intro/scanningbar.png", 121, 13),
        ("intro.scanningwords", "images/intro/scanningwords.png", 121, 13),

        ("intro.blankscan", "images/intro/blankscan.png", 121, 172),
        ("intro.invertedsprites", "images/intro/invertedsprites.png", 121, 172),
        ("intro.invertedscan", "images/intro/invertedscan.png", 121, 172),

        ("intro.jeffscan", "images/intro/jeffscan.png", 121, 172),
        ("intro.brittascan", "images/intro/brittascan.png", 121, 172),
        ("intro.abedscan", "images/intro/abedscan.png", 121, 172),
        ("intro.shirleyscan", "images/intro/shirleyscan.png", 121, 172),
        ("intro.anniescan", "images/intro/anniescan.png", 121, 172),
        ("intro.troyscan", "images/intro/troyscan.png", 121, 172),
        ("intro.piercescan", "images/intro/piercescan.png", 121, 172)
      )
    )
  }

  val rtime = 10.0
  val ctime = rtime / 7
  val ftime = ctime / 3
  val stime = ctime - ftime
}

class IntroScanState(phaser: Game) extends GameState("introscan", phaser) {
  import IntroScanState._

  private[this] val margin = 20
  private[this] val dimensions = (400 + (margin * 2)) -> (250 + (margin * 2))
  private[this] val charOffset = 28 + margin
  private[this] val labelOffset = charOffset + 180
  private[this] var elapsed: Double = 0.0

  private[this] lazy val background = {
    AnimatedSprite(phaser, margin, margin, "intro.backgrounds", Animation("intro.bg", 0 to 6, rtime / 7, loop = true))
  }
  private[this] lazy val chars = Seq(
    // TODO Correct animations
    AnimatedSprite(phaser, charOffset, charOffset, "intro.jeffscan", Animation("intro.jeffscan", 0 until 19, ctime / 19, loop = true)),
    AnimatedSprite(phaser, charOffset, charOffset, "intro.brittascan", Animation("intro.brittascan", 0 until 19, ctime / 19, loop = true)),
    AnimatedSprite(phaser, charOffset, charOffset, "intro.abedscan", Animation("intro.abedscan", 0 until 19, ctime / 19, loop = true)),
    AnimatedSprite(phaser, charOffset, charOffset, "intro.shirleyscan", Animation("intro.shirleyscan", 0 until 19, ctime / 19, loop = true)),
    AnimatedSprite(phaser, charOffset, charOffset, "intro.anniescan", Animation("intro.anniescan", 0 until 19, ctime / 19, loop = true)),
    AnimatedSprite(phaser, charOffset, charOffset, "intro.troyscan", Animation("intro.troyscan", 0 until 19, ctime / 19, loop = true)),
    AnimatedSprite(phaser, charOffset, charOffset, "intro.piercescan", Animation("intro.piercescan", 0 until 19, ctime / 19, loop = true))
  )
  private[this] lazy val description = {
    AnimatedSprite(phaser, charOffset, labelOffset, "intro.description", Animation("intro.description", (0 until 12) :+ 11, ctime / 12, loop = true))
  }
  /*
    state.descriptionanimate = anim8.newAnimation('loop', g4('1, 1-12', '1, 12'), stime/12, {[13]=ftime})
   */

  private[this] lazy val computer = {
    AnimatedSprite(phaser, charOffset + 132, charOffset + 5 + (172 / 2), "intro.computer", Animation("intro.computer", 0 until 9, ctime / 9 / 2, loop = true))
  }

  private[this] lazy val blank = {
    AnimatedSprite(phaser, charOffset + 220, charOffset, "intro.blankscan", Animation("intro.blankscan", 0 until 12, stime / 12, loop = true))
  }
  private[this] lazy val scanningbar = {
    AnimatedSprite(phaser, charOffset + 220, labelOffset, "intro.scanningbar", Animation("intro.scanningbar", 0 until 17, ctime / 17, loop = true))
  }
  /*
    state.scanbaranimate = anim8.newAnimation('loop', g5('1, 1-16', '1, 17'), (ctime-ftime*2/5)/16, {[17]=ftime*2/5})
    state.scanwordsanimate = anim8.newAnimation('loop', g6('1, 1-4', '1, 1-4', '1, 5'), (ctime-ftime*2/5)/8, {[9]=ftime*2/5})
   */

  override def create(game: Game) = {
    val group = game.add.group(name = "intro")
    group.add(background.sprite)

    chars.foreach(c => group.add(c.sprite))
    group.add(description.sprite)

    group.add(computer.sprite)

    group.add(blank.sprite)
    group.add(scanningbar.sprite)

    game.add.audio("music.opening").play(loop = true)
  }

  override def update(game: Game) = {
    val dt = game.time.physicsElapsed
    elapsed += dt

    background.update(dt)

    val charIdx = (((elapsed % rtime) / rtime) * 7).toInt
    chars.zipWithIndex.foreach { c =>
      c._1.sprite.visible = c._2 == charIdx
      if (c._1.sprite.visible) {
        c._1.update(dt)
      }
    }
    description.update(dt)

    computer.update(dt)

    blank.update(dt)
    scanningbar.update(dt)
  }
}
