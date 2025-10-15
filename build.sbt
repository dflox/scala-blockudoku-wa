name := """blockudoku-wa"""
organization := "htwg-in-wa"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala).dependsOn(ext)

lazy val ext = ProjectRef(file("../scala-blockudoku"), "root")

scalaVersion := "3.5.0"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.2" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "htwg-in-wa.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "htwg-in-wa.binders._"
