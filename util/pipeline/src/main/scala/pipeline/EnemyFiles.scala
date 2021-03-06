package pipeline

import pipeline.file.{ListingFile, ScalaFile}
import util.LuaUtils

import scala.io.Source

object EnemyFiles {
  def process(cfg: PipelineConfig) = {
    val files = (cfg.src / "nodes" / "enemies").children.filter(LuaUtils.qualifies).toSeq.map { src =>
      val lines = Source.fromString(src.contentAsString).getLines.map(_.trim).toIndexedSeq

      val key = src.name.stripSuffix(".lua")
      val name = nameFor(key)

      val width = LuaUtils.lineFor(src.name, lines, "width = ").toInt
      val height = LuaUtils.lineFor(src.name, lines, "height = ").toInt

      val hp = LuaUtils.lineFor(src.name, lines, "hp = ").toInt
      val damage = LuaUtils.lineFor(src.name, lines, "damage = ").toInt
      val isBoss = LuaUtils.lineOpt(lines, "isBoss = ").contains("true")

      val passiveSound = LuaUtils.lineOpt(lines, "passive_sound = ")
      val attackSounds = LuaUtils.lineOpt(lines, "attack_sound = ").map(_.replaceAllLiterally("'", "")).toSeq.flatMap { s =>
        s.stripPrefix("{").stripSuffix("}").split(',').map(_.trim)
      }
      val dieSound = LuaUtils.lineOpt(lines, "die_sound = ")

      val pkg = Seq("models", "data", "enemy")
      val file = ScalaFile(pkg = pkg, key = name, root = Some("shared/src/main/scala"))

      file.addImport("models.animation", "Animation")
      file.addImport("models.template.enemy", "EnemyTemplate")

      file.add(s"object $name extends EnemyTemplate(", 1)
      file.add(s"""key = "$key",""")
      file.add(s"""name = "$name",""")
      file.add(s"""width = $width,""")
      file.add(s"""height = $height,""")
      file.add(s"""hp = $hp,""")
      file.add(s"""damage = $damage,""")
      file.add(s"""isBoss = $isBoss,""")
      file.add(s"""passiveSound = ${passiveSound.toString.replaceAllLiterally("'", "\"")},""")
      if (attackSounds.isEmpty) {
        file.add(s"""attackSounds = Nil,""")
      } else {
        file.add(s"""attackSounds = Seq(${attackSounds.map("\"" + _ + "\"").mkString(", ")}),""")
      }
      file.add(s"""dieSound = ${dieSound.toString.replaceAllLiterally("'", "\"")},""")
      val sounds = LuaUtils.findSounds(lines)
      if (sounds.isEmpty) {
        file.add(s"sounds = Seq.empty,")
      } else {
        file.add(s"sounds = Seq(${sounds.mkString(", ")}),")
      }
      val anims = LuaUtils.findAnimations("Enemy: " + name, lines)
      if (anims.isEmpty) {
        file.add(s"animations = Seq.empty")
      } else {
        file.add(s"animations = Seq(", 1)
        anims.foreach(a => file.add(a))
        file.add(s")", -1)
      }
      file.add(")", -1)

      name -> cfg.writeScalaResult(s"node/enemies/${src.name}", file.path -> file.rendered)
    }
    ListingFile.listingFile(cfg, "enemy", files.map(_._1)) ++ files.flatMap(_._2)
  }

  private[this] def nameFor(key: String) = key match {
    case "acornpod" => "AcornPod"
    case "jumpingacorn" => "JumpingAcorn"
    case "laserlotusBoss" => "LaserLotusBoss"
    case "giantacorn" => "GiantAcorn"
    case "rubberspider" => "RubberSpider"
    case _ => ExportHelper.toClassName(key)
  }
}
