name := "DiscoveryService"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  // Logging
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "ch.qos.logback" % "logback-classic" % "1.1.3",

  "com.typesafe.akka" %% "akka-actor" % "2.4.17",

  "joda-time" % "joda-time" % "2.9.7",

  "org.jsoup" % "jsoup" % "1.7.2",

  // Test Dependencies
  "org.scalactic" %% "scalactic" % "3.0.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.17" % "test",
  "org.mockito" % "mockito-core" % "1.9.5")