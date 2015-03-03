name := "mma-aggregator"

version := "0.1-SNAPSHOT"

//libraryDependencies ++= Seq(
//  jdbc,
//  anorm,
//  cache
//)

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "org.mongodb" % "casbah_2.10" % "2.8",
  "com.novus" % "salat_2.10" % "1.9.9"
)

libraryDependencies += "com.sendgrid" % "sendgrid-java" % "2.1.0" // add sendgrid dependency for heroku addon

libraryDependencies += "se.radley" % "play-plugins-salat_2.10" % "1.5.0" // MongoDB Salat plugin for Play Framework 2

play.Project.playScalaSettings
