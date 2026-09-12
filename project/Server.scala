import com.sksamuel.scapegoat.sbt.ScapegoatSbtPlugin.autoImport.{scapegoatDisabledInspections, scapegoatIgnoredFiles}
import com.typesafe.sbt.GitPlugin.autoImport.git
import com.typesafe.sbt.digest.Import._
import com.typesafe.sbt.gzip.Import._
import com.typesafe.sbt.jse.JsEngineImport.JsEngineKeys
import com.typesafe.sbt.less.Import._
import com.typesafe.sbt.packager.Keys._
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.debian.DebianPlugin
import com.typesafe.sbt.packager.docker.DockerPlugin
import com.typesafe.sbt.packager.jdkpackager.JDKPackagerPlugin
import com.typesafe.sbt.packager.linux.LinuxPlugin
import com.typesafe.sbt.packager.rpm.RpmPlugin
import com.typesafe.sbt.packager.universal.UniversalPlugin
import com.typesafe.sbt.packager.windows.WindowsPlugin
import com.typesafe.sbt.web.Import._
import com.typesafe.sbt.web.SbtWeb
import io.gatling.sbt.GatlingPlugin
import io.gatling.sbt.GatlingPlugin.autoImport._
import play.routes.compiler.InjectedRoutesGenerator
import play.sbt.PlayImport.PlayKeys
import play.sbt.routes.RoutesKeys
import webscalajs.WebScalaJS.autoImport._
import sbt.Keys._
import sbt._
import sbtassembly.AssemblyPlugin.autoImport._

object Server {
  val codegen = inputKey[Unit]("Runs the code generation pipelines.")

  private[this] val dependencies = {
    import Dependencies._
    Seq(
      Tracing.jaeger, Tracing.jaegerMetrics, Metrics.micrometer,
      Akka.actor, Akka.logging, Akka.visualMailbox, Play.filters, Play.guice, Play.ws, Play.json, Play.cache
    ) ++ Database.all ++ Seq(
      GraphQL.sangria, GraphQL.playJson, GraphQL.circe,
      Authentication.silhouette, Authentication.hasher, Authentication.persistence, Authentication.crypto,
      WebJars.jquery, WebJars.fontAwesome, WebJars.materialize, WebJars.swaggerUi,
      Utils.csv, Utils.scalaGuice, Utils.commonsIo, Utils.commonsLang, Utils.betterFiles, Utils.scopts, Utils.reftree,
      Akka.testkit, Play.test, Testing.scalaTest, Testing.gatlingCore, Testing.gatlingCharts
    )
  }

  private[this] lazy val serverSettings = Shared.commonSettings ++ Seq(
    name := Shared.projectId,
    maintainer := "Hawkthorne User <admin@hawkthorne.com>",
    description := Shared.projectName,

    resolvers += "Atlassian Public" at "https://packages.atlassian.com/maven-public/",

    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
    libraryDependencies ++= dependencies,

    codegen := {
      (run in (Pipeline.pipeline, Compile)).fullInput("").evaluated
      (run in (WikiExport.wikiExport, Compile)).fullInput("").evaluated
      (compile in Compile).value
    },

    // Play
    RoutesKeys.routesGenerator := InjectedRoutesGenerator,
    RoutesKeys.routesImport ++= Seq("util.web.QueryStringUtils._", "util.web.ModelBindables._"),
    PlayKeys.externalizeResources := false,
    PlayKeys.devSettings := Seq("play.server.akka.requestTimeout" -> "infinite"),
    PlayKeys.playDefaultPort := Shared.projectPort,
    PlayKeys.playInteractionMode := PlayUtils.NonBlockingInteractionMode,

    // Scala.js
    scalaJSProjects := Seq(AdminClient.adminClient, Client.client),

    // Sbt-Web
    JsEngineKeys.engineType := JsEngineKeys.EngineType.Node,
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages ++= Seq(digest, gzip),
    includeFilter in (Assets, LessKeys.less) := "*.less",
    excludeFilter in (Assets, LessKeys.less) := "_*.less",
    LessKeys.compress in Assets := true,

    // Source Control
    scmInfo := Some(ScmInfo(url("https://github.com/KyleU/hawkthorne"), "git@github.com:KyleU/hawkthorne.git")),
    git.remoteRepo := scmInfo.value.get.connection,

    // Fat-Jar Assembly
    assemblyJarName in assembly := Shared.projectId + ".jar",
    assemblyMergeStrategy in assembly := {
      case "play/reference-overrides.conf" => MergeStrategy.concat
      case x => (assemblyMergeStrategy in assembly).value(x)
    },
    fullClasspath in assembly += Attributed.blank(PlayKeys.playPackageAssets.value),

    scapegoatIgnoredFiles := Seq(".*/Routes.scala", ".*/RoutesPrefix.scala", ".*/*ReverseRoutes.scala", ".*/*.template.scala"),
    scapegoatDisabledInspections := Seq("UnusedMethodParameter"),

    // Benchmarking
    scalaSource in Gatling := baseDirectory.value / "test"
  )

  lazy val server = Project(id = Shared.projectId, base = file(".")).enablePlugins(
    SbtWeb, play.sbt.PlayScala, JavaAppPackaging, diagram.ClassDiagramPlugin, GatlingPlugin,
    UniversalPlugin, LinuxPlugin, DebianPlugin, RpmPlugin, DockerPlugin, WindowsPlugin, JDKPackagerPlugin
  ).settings(serverSettings: _*).settings(Packaging.settings: _*).dependsOn(Shared.sharedJvm, Pipeline.pipeline)
}
