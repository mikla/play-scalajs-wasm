import org.scalajs.jsenv.nodejs.NodeJSEnv
import org.scalajs.linker.interface.ModuleSplitStyle

ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "2.13.12"
ThisBuild / version      := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .aggregate(server, client, shared.jvm, shared.js)

lazy val server = project
  .settings(
    scalaJSProjects := Seq(client),
    Assets / pipelineStages  := Seq(scalaJSPipeline),
    pipelineStages := Seq(digest, gzip),
    // triggers scalaJSPipeline when using compile or continuous compilation
    Compile / compile := ((Compile / compile) dependsOn scalaJSPipeline).value,
    libraryDependencies += guice,
    libraryDependencies += "com.vmunier" %% "scalajs-scripts" % "1.3.0"
  )
  .enablePlugins(PlayScala)
  .dependsOn(shared.jvm)

lazy val client = project
  .settings(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.0"
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb)
  .settings(
    scalaJSLinkerConfig := {
      scalaJSLinkerConfig.value
        .withExperimentalUseWebAssembly(true) // use the Wasm backend
        .withModuleKind(ModuleKind.ESModule)  // required by the Wasm backend
        .withModuleSplitStyle(ModuleSplitStyle.FewestModules)
    },
  )
  .settings(
    jsEnv := {
      val config = NodeJSEnv.Config()
        .withArgs(List(
          "--experimental-wasm-exnref", // required
          "--experimental-wasm-imported-strings", // optional (good for performance)
          "--turboshaft-wasm", // optional, but significantly increases stability
        ))
      new NodeJSEnv(config)
    }
  )
  .dependsOn(shared.js)

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .jsConfigure(_.enablePlugins(ScalaJSWeb))
