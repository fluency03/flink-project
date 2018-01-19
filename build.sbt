resolvers in ThisBuild ++= Seq("Apache Development Snapshot Repository" at "https://repository.apache.org/content/repositories/snapshots/",
  Resolver.mavenLocal)

lazy val commonSettings = Seq(
  organization := "com.fluency03.flink",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.8"
)

scalaVersion in ThisBuild := "2.11.8"

val flinkVersion = "1.4.0"

val flinkDependencies = Seq(
  "org.apache.flink" %% "flink-scala" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-clients" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-connector-wikiedits" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-connector-twitter" % "1.4.0" % "provided")

libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.2"

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= flinkDependencies
  )

mainClass in assembly := Some("com.fluency03.flink.WikipediaEditMonitoring")

mainClass in (Compile, packageBin) := Some("com.fluency03.flink.WikipediaEditMonitoring")

// make run command include the provided dependencies
run in Compile := Defaults.runTask(fullClasspath in Compile,
                                   mainClass in (Compile, run),
                                   runner in (Compile,run)
                                  ).evaluated

// exclude Scala library from assembly
assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
