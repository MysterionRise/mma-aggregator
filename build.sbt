name := "mma-aggregator"

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "org.mongodb" %% "casbah" % "2.8.0",
  "com.novus" %% "salat" % "1.9.9",
  "com.sendgrid" % "sendgrid-java" % "2.1.0",
  "se.radley" %% "play-plugins-salat" % "1.5.0"
)

play.Project.playScalaSettings
