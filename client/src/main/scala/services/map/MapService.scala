package services.map

import com.definitelyscala.phaserce._
import models.asset.Asset
import models.data.map.TiledMap
import models.options.SystemOptions
import services.audio.MusicService
import util.PhaserUtils

import scala.scalajs.js

object MapService {
  def assetsFor(map: TiledMap) = Seq(
    MusicService.asset(map.soundtrack),
    Asset.Tilemap(s"map.${map.value}", s"maps/${map.value}.json")
  ) ++ map.tilesets.map(ts => Asset.Image(ts.name, s"images/tilesets/${ts.img}.png"))
}

class MapService(game: Game, val map: TiledMap, playMusic: Boolean) {
  val group = game.add.group(name = s"map.${map.value}")

  val tilemap = new Tilemap(game, s"map.${map.value}")
  map.tilesets.foreach(ts => tilemap.addTilesetImage(tileset = ts.name))

  val backdrop = PhaserUtils.makeBackdrop(game = game, width = tilemap.widthInPixels, height = tilemap.heightInPixels, color = map.color)
  group.add(backdrop)

  val bgMusic = MusicService.play(map.soundtrack, loop = true)

  val mapPxWidth = map.width * SystemOptions.tileSize
  val mapPxHeight = map.height * SystemOptions.tileSize

  val layers = tilemap.layers.map(_.asInstanceOf[js.Dynamic].name.toString).map { k =>
    val l = tilemap.createLayer(k)
    l.name = k
    l.resize(mapPxWidth.toDouble, mapPxHeight.toDouble)
    l
  }
  layers.foreach(l => group.add(l))

  def layer(key: String) = layers.find(_.name == key)

  val collisionLayer = layer("collision")
  collisionLayer.foreach { c =>
    c.visible = false
  }

  val parallaxLayers = layers.flatMap { l =>
    val parallax = l.layer.asInstanceOf[js.Dynamic].properties.parallax.toString match {
      case "undefined" => None
      case x => Some(x.toDouble)
    }
    parallax.map(l -> _)
  }

  def destroy() = {
    tilemap.destroy()
    bgMusic.stop()
    group.destroy()
  }
}
