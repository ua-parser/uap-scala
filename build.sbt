import ReleaseTransformations.*

val commonScalacOptions = Seq(
  "-Xfatal-warnings",
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-unchecked"
)

val scalac2Flags = Seq(
  "-Xlint:adapted-args",
  "-Xsource:3",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused:imports"
)

lazy val commonSettings = Seq(
  scalaVersion       := "2.13.14",
  crossScalaVersions := Seq("2.12.20", "2.13.16", "3.3.6"),
  scalacOptions      := {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((3, _)) =>
        commonScalacOptions :+ "-language:implicitConversions"
      case Some((2, _)) =>
        commonScalacOptions ++ scalac2Flags
      case _            =>
        commonScalacOptions
    }
  }
)

lazy val lib = project
  .in(file("modules/lib"))
  .settings(commonSettings *)
  .settings(
    name                  := "uap-scala",
    organization          := "org.uaparser",
    libraryDependencies ++= Seq(
      "org.yaml" % "snakeyaml" % "2.4",
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, _))                              =>
          "org.specs2" %% "specs2-core" % "5.6.4" % "test"
        case Some((2, scalaMajor)) if scalaMajor >= 11 =>
          "org.specs2" %% "specs2-core" % "4.21.0" % "test"
        case _                                         =>
          "org.specs2" %% "specs2-core" % "3.10.0" % "test"
      }
    ),
    mimaPreviousArtifacts := Set("org.uaparser" %% "uap-scala" % "0.3.0"),

    // make sure we include necessary uap-core files
    Compile / unmanagedResourceDirectories += (ThisBuild / baseDirectory).value / "core",
    Compile / unmanagedResources / includeFilter := "regexes.yaml",
    Test / unmanagedResourceDirectories += (ThisBuild / baseDirectory).value / "core",
    Test / unmanagedResources / includeFilter    := "*.yaml",
    Test / publishArtifact                       := false,
    publishMavenStyle                            := true,
    publishTo                                    := {
      val centralSnapshots = "https://central.sonatype.com/repository/maven-snapshots/"
      if (isSnapshot.value) Some("central-snapshots" at centralSnapshots)
      else localStaging.value
    }
  )

lazy val benchmark = project
  .in(file("modules/benchmark"))
  .settings(commonSettings *)
  .dependsOn(lib)
  .enablePlugins(JmhPlugin)
  .settings(
    name                  := "uap-scala-benchmark",
    Jmh / run / mainClass := Some("org.uaparser.scala.benchmark.Main"),
    publish / skip        := true
  )

// Publishing settings
releaseCrossBuild    := true
releaseTagComment    := s"Release ${(ThisBuild / version).value}"
releaseCommitMessage := s"Set version to ${(ThisBuild / version).value}"
releaseProcess       := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("+publishSigned"),
  releaseStepCommand("sonaRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
ThisBuild / pomExtra := (<url>https://github.com/ua-parser/uap-scala</url>
  <licenses>
    <license>
      <name>WTFPL</name>
      <url>http://www.wtfpl.net/about</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:ua-parser/uap-scala.git</url>
    <connection>scm:git:git@github.com:ua-parser/uap-scala.git</connection>
  </scm>
  <developers>
    <developer>
      <id>mcveat</id>
      <name>Piotr Adamski</name>
      <url>https://twitter.com/mcveat</url>
    </developer>
    <developer>
      <id>humanzz</id>
      <name>Ahmed Sobhi</name>
      <url>https://twitter.com/humanzz</url>
    </developer>
    <developer>
      <id>travisbrown</id>
      <name>Travis Brown</name>
      <url>https://twitter.com/travisbrown</url>
    </developer>
    <developer>
      <id>phucnh</id>
      <name>Nguyen Hong Phuc</name>
      <url>https://twitter.com/phuc89</url>
    </developer>
  </developers>)

// do not cross build or publish the aggregating root
crossScalaVersions := Nil
publish / skip     := true
