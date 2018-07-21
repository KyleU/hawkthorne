package services.node

import com.definitelyscala.phaserce.{Game, Group}
import models.component.StaticSprite
import models.node.{MovingPlatformNode, Node, SimpleNode}
import services.map.MapService

object MovingPlatformComponents {
  def apply(game: Game, group: Group, n: MovingPlatformNode, nodes: Seq[Node]) = {
    val name = if (n.name.isEmpty) { n.sheetKey } else { n.name }

    val chainLength = n.properties.chain.map(_.toInt).getOrElse(1)
    val sprites = (0 until chainLength).map { idx =>
      val x = (n.x + (n.width * idx)) * MapService.scaleInt
      StaticSprite(game = game, group = group, name = s"$name $idx", x = x, y = n.y * MapService.scaleInt, key = n.sheetKey)
    }

    val lineNode = nodes.collectFirst {
      case s: SimpleNode if s.name == n.properties.line => s
    }.getOrElse(throw new IllegalStateException(s"Cannot find simple node for referenced line [${n.properties.line}]."))

    lineNode.polyline.getOrElse(throw new IllegalStateException(s"Line [${lineNode.name}] has no defined points."))

    sprites
  }
}
