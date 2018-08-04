package services.options

import com.definitelyscala.phaserce.{Game, Point}
import models.component.Menu
import models.input.{MenuAction, PointerAction}
import models.settings.ClientSettings

class OptionsMenu(game: Game, onExit: () => Unit) {
  private[this] val size = 380.0
  private[this] var zoom = 1.0
  private[this] var inMain = true

  private[this] val menu = Menu(
    game = game, name = "options.menu", fontKey = "big", arrowKey = "options.menu.arrow", backgroundKey = "options.menu.bg",
    width = 272, height = 176, margin = 12.0, lineHeight = 26.0, fontColor = "#000000", fontOffset = 4.0
  )
  game.stage.add(menu.group)
  private[this] val (mw, mh) = (menu.width * menu.group.scale.x, menu.height * menu.group.scale.y)

  private[this] val comps = new OptionsComponents(game, menu, ClientSettings.getSettings)

  private[this] def onSelect(key: String): Unit = key match {
    case "costume" => util.Logging.info("TODO")
    case "game" => switchToGame()
    case "audio" => switchToAudio()
    case "video" => switchToVideo()
    case "x" => util.Logging.info("TODO")

    case "hardcore" => comps.hardcoreCheckbox.toggle()
    case "tutorial" => comps.tutorialCheckbox.toggle()

    case "music" => comps.musicMeter.increment()
    case "sfx" => comps.sfxMeter.increment()

    case "fullscreen" => comps.fullscreenCheckbox.toggle()
    case "fps" => comps.fpsCheckbox.toggle()

    case "back" if inMain => onExit()
    case "back" => switchToMain()

    case _ => throw new IllegalStateException(s"Unhandled option menu key [$key].")
  }

  private[this] def action(act: (String, String)) = act._1 -> (() => onSelect(act._2))

  val mainActs = IndexedSeq("Costume" -> "costume", "Game" -> "game", "Audio" -> "audio", "Video" -> "video", "X" -> "x", "Back" -> "back").map(action)
  def switchToMain() = {
    inMain = true
    menu.setOptions(mainActs)
    comps.switchToMain()
  }

  switchToMain()

  val gameActs = IndexedSeq("Hardcore" -> "hardcore", "Tutorials" -> "tutorial", "Back" -> "back").map(action)
  def switchToGame() = {
    inMain = false
    menu.setOptions(gameActs)
    comps.switchToGame()
  }

  val audioActs = IndexedSeq("Music" -> "music", "SFX" -> "sfx", "Back" -> "back").map(action)
  def switchToAudio() = {
    inMain = false
    menu.setOptions(audioActs)
    comps.switchToAudio()
  }

  val videoActs = IndexedSeq("Fullscreen" -> "fullscreen", "Show FPS" -> "fps", "Back" -> "back").map(action)
  def switchToVideo() = {
    inMain = false
    menu.setOptions(videoActs)
    comps.switchToVideo()
  }

  def menuActions(acts: Seq[MenuAction]) = acts.foreach(menu.onMenuAction)

  def onPointer(act: PointerAction) = {
    val mp = menu.group.position
    val (x, y) = ((act.worldX - menu.group.worldPosition.x) / zoom, (act.worldY - menu.group.worldPosition.y) / zoom)
    if (x >= 0 && x <= mw && y >= 0 && y <= mh) {
      val idx = ((y + menu.margin) / menu.lineHeight).toInt - 1
      if (idx >= 0 && idx < menu.optionCount) {
        menu.setActiveOption(idx)
        menu.onSelect()
      }
    }
  }

  def resize(width: Int, height: Int) = {
    zoom = Math.min(width / size, height / size)
    menu.group.scale = new Point(zoom, zoom)
    menu.x = (width - (menu.background.width * zoom)) / 2
    menu.y = (height - (menu.background.height * zoom)) / 2
  }
}
