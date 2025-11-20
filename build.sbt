name := """blockudoku-wa"""
organization := "htwg-in-wa"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala).dependsOn(ext)

lazy val ext = ProjectRef(file("../scala-blockudoku"), "root")

scalaVersion := "3.5.0"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.2" % Test
//libraryDependencies ++= Seq(
//  "io.grpc" % "grpc-netty" % "1.77.0",
//  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % "1.0.0-alpha.3"
//)

val AkkaVersion = "2.6.20"
val AkkaHttpVersion = "10.2.10"

libraryDependencies ++= Seq(

  // Akka gRPC
  "com.lightbend.akka.grpc" %% "akka-grpc-runtime" % "2.1.6",

  // You may need these if not already provided by Play
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http2-support" % AkkaHttpVersion
)

// Enable the Akka gRPC plugin
enablePlugins(AkkaGrpcPlugin)
