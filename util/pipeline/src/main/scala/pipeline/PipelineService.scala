package pipeline

import scala.concurrent.Await
import scala.concurrent.duration._

object PipelineService {
  def main(args: Array[String]): Unit = run()

  def run() = {
    val cfg = PipelineConfig(srcProjectLoc = "../../Libraries/hawkthorne-journey/src", tgtRootLoc = ".", wipe = false)
    val resultF = PipelineService.go(cfg)
    Await.result(resultF, 10.seconds)
  }

  def go(cfg: PipelineConfig) = {
    val started = util.DateUtils.now
    val startNanos = System.nanoTime

    if (cfg.wipe) {
      cfg.assetRoot.children.foreach(_.delete(swallowIOExceptions = true))
      cfg.scalaAppRoot.children.foreach(_.delete(swallowIOExceptions = true))
      cfg.scalaSharedRoot.children.foreach(_.delete(swallowIOExceptions = true))
    }

    val files = {
      AnimationFiles.process(cfg) ++
        AudioFiles.process(cfg) ++
        CharacterFiles.process(cfg) ++
        EnemyFiles.process(cfg) ++
        EpisodeFiles.process(cfg) ++
        InventoryItemFiles.process(cfg) ++
        MapFiles.process(cfg) ++
        NpcFiles.process(cfg) ++
        ProjectileFiles.process(cfg) ++
        QuestFiles.process(cfg) ++
        VehicleFiles.process(cfg) ++
        WeaponFiles.process(cfg)
    }
    val result = PipelineResult(config = cfg, started = started, durationNanos = System.nanoTime - startNanos, files = files)
    scala.concurrent.Future.successful(result)
  }
}
