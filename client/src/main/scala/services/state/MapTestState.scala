package services.state

import com.definitelyscala.phaserce.Game
import models.component.BaseComponent
import models.data.map.TiledMap
import models.phaser.SplashScreen
import services.map.{MapNodeParser, MapService}
import services.node.NodeLoader

object MapTestState {
  def load(phaser: Game, map: TiledMap) = new LoadingState(next = new MapTestState(phaser, map), phaser = phaser, assets = MapService.assetsFor(map))
}

class MapTestState(phaser: Game, map: TiledMap) extends GameState(map.value, phaser) {
  private[this] val components = collection.mutable.ArrayBuffer.empty[BaseComponent]

  override def create(game: Game) = {
    val mapSvc = new MapService(game, map, playMusic = true)
    val nodes = MapNodeParser.parse(game.cache.getTilemapData("map." + map.value))

    val (progress, splashComplete) = SplashScreen.show(game)
    new NodeLoader(game, mapSvc.group, progress).load(nodes = nodes, onComplete = newComponents => {
      components ++= newComponents
      splashComplete()
    })
  }

  override def update(game: Game) = {
    val dt = game.time.physicsElapsed
    components.foreach(_.update(dt))
  }
}
