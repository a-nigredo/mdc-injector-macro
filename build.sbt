name := "mdc-injector"

version := "1.0"

scalaVersion := "2.11.11"

scalacOptions in Test ++= Seq("-Yrangepos")

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-compiler" % scalaVersion.value,
  "org.specs2" %% "specs2-core" % "3.9.1" % "test",
  "org.specs2" %% "specs2-scalacheck" % "3.9.4" % "test",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % "test;compile;provided",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2" % "test;compile;provided",
  compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
)

crossPaths := false

lazy val artName = Def.task(s"${name.value}-${(version in ThisBuild).value}.jar")

assemblyJarName in assembly := artName.value