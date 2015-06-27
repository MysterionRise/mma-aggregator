import com.heroku.sbt.HerokuPlugin.autoImport._
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
  herokuAppName in Compile := "psycho-test-framework",
  herokuSkipSubProjects in Compile := false,
  herokuProcessTypes in Compile := Map(
    "web" -> "target/universal/stage/bin/exampleserver -Dhttp.port=$PORT -Dhttp.netty.maxInitialLineLength=81920"
  ),
  herokuConfigVars in Compile := Map(
    "JAVA_OPTS" -> "$JAVA_OPTS -Dhttp.netty.maxInitialLineLength=81920s"
  )

).
  enablePlugins(PlayScala).
  aggregate(clients.map(projectToRef): _*).
  dependsOn(exampleSharedJvm)

lazy val exampleClient = (project in file("psycho-test-client")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  persistLauncher in Test := false,
  sourceMapsDirectories += exampleSharedJs.base / "..",
  unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value),
  jsDependencies += "org.webjars" % "react" % "0.12.1" /
    "react-with-addons.js" commonJSName "React",
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.8.0",
    "com.github.japgolly.scalajs-react" %%% "core" % "0.8.4",
    "com.github.marklister" %%% "base64" % "0.1.1"
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
