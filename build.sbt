name := """blockudoku-wa"""
organization := "htwg-in-wa"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala).dependsOn(ext)

lazy val ext = ProjectRef(file("../scala-blockudoku"), "root")

scalaVersion := "3.5.0"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.2" % Test
libraryDependencies ++= Seq(
  "io.grpc" % "grpc-netty" % "1.77.0",
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % "1.0.0-alpha.3"
)
