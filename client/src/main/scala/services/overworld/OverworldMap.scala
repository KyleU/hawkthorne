package services.overworld

import com.definitelyscala.phaserce.{Game, Group, TileSprite}
import models.input.MenuAction
import models.options.GameOptions
import models.player.Player
import services.camera.CameraService
import services.input.InputService
import services.navigation.NavigationPaths
import services.state.NavigationService

case class OverworldMap(game: Game, inputService: InputService, player: Player, initialZone: String) {
  private[this] val padding = 200.0
  private[this] val dimensions = (2328.0 + padding) -> (1344.0 + padding)
  private[this] var elapsed = 0.0

  val group = new Group(game, name = "overworld")

  private[this] val camera = new CameraService(
    game = game,
    group = group,
    parallaxLayers = Nil,
    desiredSize = 600 -> 600,
    pxSize = dimensions._1.toInt -> dimensions._2.toInt
  )

  val background = new TileSprite(game = game, x = 0, y = 0, width = dimensions._1, height = dimensions._2, key = "overworld.water")
  group.add(background)

  val staticComponents = new OverworldStaticComponents(game, group)
  val movement = new OverworldMovement(game, group, player, initialZone, camera)
  staticComponents.addOverlays()

  val clouds = (0 until 15).map(idx => OverworldCloud(idx, group, dimensions))

  def menuAct(a: MenuAction) = a match {
    case MenuAction.Select => movement.current.map.foreach { newMap =>
      val newOpts = GameOptions(map = newMap)
      val next = NavigationPaths.newGameState(game, inputService, newOpts)
      NavigationService.navigateTo(game, next, path = Some(s"map/${newMap.value}"))
    }
    case _ => movement.onInput(a)
  }

  def update(dt: Double) = {
    elapsed = elapsed + dt
    val mod = (elapsed * 1000).toInt % 1000
    background.frame = if (mod > 500) { 0 } else { 1 }
    clouds.foreach(_.update(dt))
    staticComponents.update(dt)
    movement.update(dt)
  }

  def resize(width: Int, height: Int) = {
    camera.resize(width, height)
    movement.titleboard.resize(camera.currentZoom)
  }

  def destroy() = {
    group.destroy()
    movement.titleboard.destroy()
  }
}
