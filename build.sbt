lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "iglu-json-schema-validator",
    version := "0.1",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      guice,
      "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test
    ),
  )