package services.intro

import models.asset.Asset
import models.font.Font
import services.audio.{MusicService, SoundEffectService}

object IntroAssets {
  val charWidth = 121
  val charHeight = 172

  val assets = Seq(
    MusicService.asset("opening"),

    Asset.Spritesheet("intro.backgrounds", "images/scanning/backgrounds.png", 400, 250),
    Asset.Spritesheet("intro.names", "images/scanning/names.png", 75, 15),
    Asset.Spritesheet("intro.computer", "images/scanning/computer.png", 75, 19),
    Asset.Spritesheet("intro.description", "images/scanning/description.png", charWidth, 13),
    Asset.Spritesheet("intro.scanningbar", "images/scanning/scanningbar.png", charWidth, 13),
    Asset.Spritesheet("intro.scanningwords", "images/scanning/scanningwords.png", charWidth, 13),

    Asset.Spritesheet("intro.blankscan", "images/scanning/blankscan.png", charWidth, charHeight),
    Asset.Spritesheet("intro.invertedsprites", "images/scanning/invertedsprites.png", charWidth, charHeight),
    Asset.Spritesheet("intro.invertedscan", "images/scanning/invertedscan.png", charWidth, charHeight),

    Asset.Spritesheet("intro.jeffscan", "images/scanning/jeffscan.png", charWidth, charHeight),
    Asset.Spritesheet("intro.brittascan", "images/scanning/brittascan.png", charWidth, charHeight),
    Asset.Spritesheet("intro.abedscan", "images/scanning/abedscan.png", charWidth, charHeight),
    Asset.Spritesheet("intro.shirleyscan", "images/scanning/shirleyscan.png", charWidth, charHeight),
    Asset.Spritesheet("intro.anniescan", "images/scanning/anniescan.png", charWidth, charHeight),
    Asset.Spritesheet("intro.troyscan", "images/scanning/troyscan.png", charWidth, charHeight),
    Asset.Spritesheet("intro.piercescan", "images/scanning/piercescan.png", charWidth, charHeight),

    Asset.Image("intro.cityscape", "images/menu/cityscape.png"),
    Asset.Spritesheet("intro.beams", "images/menu/beams.png", 99, 99),
    Asset.Image("intro.logo", "images/menu/logo.png"),

    Asset.Image(s"intro.menu.arrow", s"images/menu/small_arrow.png"),
    Asset.Image("intro.menu.bg", "images/openingmenu.png"),

    models.node.SparkleNode.asset
  ) ++ SoundEffectService.menuAssets ++ Font.assets
}
