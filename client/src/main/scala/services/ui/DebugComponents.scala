package services.ui

import com.definitelyscala.datgui.GUI
import models.component._
import util.DatGuiUtils

object DebugComponents {
  def add(root: GUI, c: BaseComponent) = {
    val f = root.addFolder(c.name)
    c match {
      case as: AnimatedSprite => f.add(as.sprite, "visible")
      case cl: ConsoleLog => f.add(cl.group, "visible")
      case ho: HudOverlay => f.add(ho.group, "visible")
      case lc: LiquidComponent => // TODO
      case si: StaticImage => f.add(si.image, "visible")
      case ss: StaticSprite => f.add(ss.sprite, "visible")
    }
    DatGuiUtils.addFunction(gui = f, title = "Debug", f = () => util.Logging.info(c.toString))
  }
}
