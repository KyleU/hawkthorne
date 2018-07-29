package models.phaser

import com.definitelyscala.phaserce._

object SplashScreen {
  def show(game: Game, x: Int = 0, y: Int = 0) = {
    val solidBlack = game.make.bitmapData(1, 1)
    solidBlack.fill(0, 0, 0)

    val backdrop = game.add.sprite(0, 0, solidBlack)
    backdrop.width = game.width
    backdrop.height = game.height

    val splash = game.add.sprite(game.width / 2, game.height / 2, "splash")
    splash.anchor = util.PhaserUtils.centerPoint
    val splashScale = game.width * 0.6 / splash.width
    splash.scale = new Point(splashScale, splashScale)

    val progress = game.add.sprite(game.width / 2, (game.height / 2) + (100 * splashScale), "progress", 16)
    progress.anchor = util.PhaserUtils.centerPoint
    progress.scale = new Point(splashScale, splashScale)

    progress -> (() => {
      game.world.removeChild(backdrop)
      game.world.removeChild(splash)
      game.world.removeChild(progress)
      ()
    })
  }
}
