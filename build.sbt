lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "iglu-json-schema-validator",
    version := "0.1",
    scalaVersion := "2.12.8",
    libraryDependencies ++= Seq(
      guice,
      ws,
      "com.snowplowanalytics" %% "iglu-core" % "0.5.1",
      "com.github.java-json-tools" % "json-schema-validator" % "2.2.11",
      "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test,
      specs2 % Test
    ),
  )

parallelExecution in Test := false