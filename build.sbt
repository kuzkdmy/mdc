ThisBuild / scalaVersion := "2.13.13"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.test"
ThisBuild / organizationName := "mdc"

lazy val root = (project in file("."))
  .settings(
    name := "mdc",
    libraryDependencies ++= Seq(
      "org.typelevel"       %% "cats-core"                % "2.10.0",
      "org.typelevel"       %% "cats-effect"              % "3.5.4",
      "ch.qos.logback"       % "logback-classic"          % "1.5.3",
      "net.logstash.logback" % "logstash-logback-encoder" % "7.4"
    )
  )
