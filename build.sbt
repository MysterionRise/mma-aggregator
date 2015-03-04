import play.PlayScala

name := "mma-aggregator"

version := "0.1-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws
)

libraryDependencies += "org.postgresql" % "postgresql" % "9.4-1201-jdbc41"
