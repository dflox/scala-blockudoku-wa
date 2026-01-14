name := """blockudoku-wa"""
organization := "htwg-in-wa"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala).dependsOn(ext)

lazy val ext = ProjectRef(file("../scala-blockudoku"), "root")

scalaVersion := "3.5.0"

val pac4jVersion = "6.3.1"
libraryDependencies ++= Seq(
  "org.pac4j" %% "play-pac4j" % "13.0.0-PLAY3.0",
  "org.pac4j" % "pac4j-http" % pac4jVersion excludeAll(ExclusionRule(organization = "com.fasterxml.jackson.core")),
  "org.pac4j" % "pac4j-jwt" % pac4jVersion exclude("commons-io" , "commons-io"),
  "org.pac4j" % "pac4j-oauth" % pac4jVersion excludeAll(ExclusionRule(organization = "com.fasterxml.jackson.core"))
)

dependencyOverrides ++= Seq(
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.19.2",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.19.2",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.19.2",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.19.2"
)

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.2" % Test
libraryDependencies += ws