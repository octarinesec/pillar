import sbt._
import Keys._
import xerial.sbt.Sonatype

fork in Test := true

val assemblyTestSetting = test in assembly := {}

assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", xs @ _ *)         => MergeStrategy.first
  case PathList(ps @ _ *) if ps.last endsWith ".html" => MergeStrategy.first
  case "META-INF/io.netty.versions.properties"        => MergeStrategy.last
  case "application.conf"                             => MergeStrategy.concat
  case "unwanted.txt"                                 => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

val dependencies = Seq(
  "com.datastax.cassandra" % "cassandra-driver-core" % "3.1.4",
  "org.cassandraunit" % "cassandra-unit" % "3.1.3.2" % "test",
  "com.typesafe" % "config" % "1.3.1",
  "org.mockito" % "mockito-core" % "2.8.47" % "test",
  "org.scalatest" %% "scalatest" % "3.0.3" % "test",
  "com.google.guava" % "guava" % "18.0" % "test",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % "test"
)

val rpmSettings = {
  Seq(
    rpmVendor := "kaufhof",
    rpmUrl := Some("https://github.com/Galeria-Kaufhof/pillar"),
    rpmGroup := Some("Development/Others"),
    linuxPackageMappings in Rpm := linuxPackageMappings.value,
    packageName in Rpm := "de.kaufhof",
    packageDescription in Rpm := """Pillar manages migrations for your Cassandra data stores.""",
    version in Rpm := version.value,
    rpmRelease in Rpm := "1",
    packageSummary in Rpm := "Pillar manages migrations for your Cassandra data stores.",
    rpmLicense := Some("http://www.opensource.org/licenses/mit-license.php")
  )
}

lazy val root = Project(
  id = "pillar",
  base = file("."),
  settings = Defaults.coreDefaultSettings ++ Sonatype.sonatypeSettings
).settings(
    assemblyTestSetting,
    libraryDependencies ++= dependencies,
    name := "pillar",
    organization := "de.kaufhof",
    version := "4.1.0",
    homepage := Some(url("https://github.com/Galeria-Kaufhof/pillar")),
    licenses := Seq("MIT license" -> url(
      "http://www.opensource.org/licenses/mit-license.php")),
    scalaVersion := "2.12.2",
    crossScalaVersions := Seq("2.12.2", "2.11.11")
  )
  .settings(
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    parallelExecution in Test := false,
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ =>
      false
    },
    pomExtra := (
      <scm>
        <url>git@github.com:Galeria-Kaufhof/pillar.git</url>
        <connection>scm:git:git@github.com:Galeria-Kaufhof/pillar.git</connection>
      </scm>
        <developers>
          <developer>
            <id>marcopriebe</id>
            <name>MarcoPriebe</name>
            <url>https://github.com/MarcoPriebe</url>
          </developer>
          <developer>
            <id>lichtsprung</id>
            <name>Robert Giacinto</name>
            <url>https://github.com/lichtsprung</url>
          </developer>
          <developer>
            <id>adelafogoros</id>
            <name>Adela Fogoros</name>
            <url>https://github.com/adelafogoros</url>
          </developer>
          <developer>
            <id>muellenborn</id>
            <name>Markus Müllenborn</name>
            <url>https://github.com/muellenborn</url>
          </developer>
        </developers>
    )
  )
  .settings(rpmSettings)
  .enablePlugins(RpmPlugin)
