ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

val AkkaVersion = "2.7.0"

lazy val root = (project in file("."))
  .settings(
    name := "Multilink"
  )

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.monovore" %% "decline" % "2.4.1"
  )