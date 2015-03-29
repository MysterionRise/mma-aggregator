import sbt.Keys._
import sbt.Project.projectToRef

lazy val clients = Seq(exampleClient)
lazy val scalaV = "2.11.6"

lazy val exampleServer = (project in file("psycho-test-server")).settings(
  scalaVersion := scalaV,
  scalaJSProjects := clients,
  pipelineStages := Seq(scalaJSProd, gzip),
  libraryDependencies ++= Seq(
    "com.vmunier" %% "play-scalajs-scripts" % "0.1.0",
    "org.webjars" % "jquery" % "1.11.1",
    "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
    "com.typesafe.slick" %% "slick" % "2.1.0",
    "com.typesafe.play" %% "play-slick" % "0.8.1"
  ),
  EclipseKeys.skipParents in ThisBuild := false).
  enablePlugins(PlayScala).
  aggregate(clients.map(projectToRef): _*).
  dependsOn(exampleSharedJvm)

lazy val exampleClient = (project in file("psycho-test-client")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  persistLauncher in Test := false,
  sourceMapsDirectories += exampleSharedJs.base / "..",
  unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value),
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.8.0"
  )).
  enablePlugins(ScalaJSPlugin, ScalaJSPlay).
  dependsOn(exampleSharedJs)

lazy val exampleShared = (crossProject.crossType(CrossType.Pure) in file("shared-module")).
  settings(scalaVersion := scalaV).
  jsConfigure(_ enablePlugins ScalaJSPlay).
  jsSettings(sourceMapsBase := baseDirectory.value / "..")

lazy val exampleSharedJvm = exampleShared.jvm
lazy val exampleSharedJs = exampleShared.js

// loads the jvm project at sbt startup
onLoad in Global := (Command.process("project exampleServer", _: State)) compose (onLoad in Global).value
