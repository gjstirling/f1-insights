name := """f1-insights"""
organization := "com.gjstirling"
version := "1.0-SNAPSHOT"
scalaVersion := "2.13.14"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test,
  "com.lihaoyi" %% "requests" % "0.8.0",
  "com.lihaoyi" %% "upickle" % "3.2.0",
  "org.mongodb.scala" %% "mongo-scala-driver" % "5.1.1",
  "org.apache.pekko" %% "pekko-actor" % "1.0.0",
  "org.apache.pekko" %% "pekko-slf4j" % "1.0.0",
  filters
)

coverageExcludedPackages := "<empty>;Reverse.*;router;main.scala.config*;.*MyLogger.*;.*MyLocalRepo.*;.*F1OpenApiClient.*\\.*"

import com.typesafe.sbt.packager.docker._
dockerChmodType := DockerChmodType.UserGroupWriteExecute
dockerPermissionStrategy := DockerPermissionStrategy.CopyChown
