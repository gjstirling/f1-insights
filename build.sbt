name := """f1-insights"""
organization := "com.gjstirling"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.14"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test
libraryDependencies += "com.lihaoyi" %% "requests" % "0.8.0"
libraryDependencies += "com.lihaoyi" %% "upickle" % "3.2.0"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.gjstirling.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.gjstirling.binders._"
