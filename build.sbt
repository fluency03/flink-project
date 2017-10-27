resolvers in ThisBuild ++= Seq("Apache Development Snapshot Repository" at "https://repository.apache.org/content/repositories/snapshots/",
  Resolver.mavenLocal)

name := "flink-project"

version := "0.1-SNAPSHOT"

organization := "org.example"

scalaVersion in ThisBuild := "2.11.8"

val flinkVersion = "1.3.2"

val flinkDependencies = Seq(
  "org.apache.flink" %% "flink-scala" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % "provided")

//scalacOptions := {
//  val out = streams.value // streams task happens-before scalacOptions
//  val log = out.log
//  log.info("123")
//  val ur = update.value   // update task happens-before scalacOptions
//  log.info("456")
//  ur.allConfigurations.take(3)
//}

//scalacOptions := {
//  val ur = update.value  // update task happens-before scalacOptions
//  if (false) {
//    val x = clean.value  // clean task happens-before scalacOptions
//  }
//  ur.allConfigurations.take(3)
//}

// error: A setting cannot depend on a task
//checksums := scalacOptions.value
// this is ok
//scalacOptions := checksums.value

lazy val root = (project in file(".")).
  settings(
    libraryDependencies ++= flinkDependencies
//    scalacOptions := List("-encoding", "utf8", "-Xfatal-warnings", "-deprecation", "-unchecked"),
//    scalacOptions := {
//      val old = scalacOptions.value
//      scalaBinaryVersion.value match {
//        case "2.11" => old
//        case _      => old filterNot (Set("-Xfatal-warnings", "-deprecation").apply)
//      }
//    }
  )

mainClass in assembly := Some("org.example.Job")

// make run command include the provided dependencies
run in Compile := Defaults.runTask(fullClasspath in Compile,
                                   mainClass in (Compile, run),
                                   runner in (Compile,run)
                                  ).evaluated

// exclude Scala library from assembly
assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
