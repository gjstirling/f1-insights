name := """f1-insights"""
organization := "com.gjstirling"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.14"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test
libraryDependencies += "com.lihaoyi" %% "requests" % "0.8.0"
libraryDependencies += "com.lihaoyi" %% "upickle" % "3.2.0"
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "5.1.0"

coverageExcludedPackages := "<empty>;Reverse.*;router;main.scala.config*;.*MyLogger.*;.*MyLocalRepo.*;.*RealApiClient.*\\.*"

import com.typesafe.sbt.packager.docker._
dockerChmodType := DockerChmodType.UserGroupWriteExecute
dockerPermissionStrategy := DockerPermissionStrategy.CopyChown



