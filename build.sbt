import ReleaseTransformations._

name         := "uap-scala"
organization := "org.uaparser"

scalaVersion       := "2.13.14"
crossScalaVersions := Seq("2.12.20", "2.13.16", "3.3.6")

scalacOptions ++= Seq(
  "-Xfatal-warnings",
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-unchecked"
)

val scala2Flags = Seq(
  "-Xlint:adapted-args",
  "-Xsource:3",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused:imports"
)

scalacOptions := {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((3, _)) =>
      scalacOptions.value :+ "-language:implicitConversions"
    case Some((2, _)) =>
      scalacOptions.value ++ scala2Flags
    case _            =>
      scalacOptions.value
  }
}

// Enable scalafix
semanticdbEnabled := true
semanticdbVersion := scalafixSemanticdb.revision

libraryDependencies += "org.yaml" % "snakeyaml" % "2.4"

libraryDependencies := {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((3, _))                              =>
      libraryDependencies.value ++ Seq("org.specs2" %% "specs2-core" % "5.5.8" % "test")
    case Some((2, scalaMajor)) if scalaMajor >= 11 =>
      libraryDependencies.value ++ Seq("org.specs2" %% "specs2-core" % "4.21.0" % "test")
    case _                                         =>
      libraryDependencies.value ++ Seq("org.specs2" %% "specs2-core" % "3.10.0" % "test")
  }
}

mimaPreviousArtifacts := Set("org.uaparser" %% "uap-scala" % "0.3.0")

Compile / unmanagedResourceDirectories += baseDirectory.value / "core"
Compile / unmanagedResources / includeFilter := "regexes.yaml"
Test / unmanagedResourceDirectories += baseDirectory.value / "core"
Test / unmanagedResources / includeFilter    := "*.yaml"

// Publishing
publishMavenStyle      := true
publishTo              := sonatypePublishToBundle.value
Test / publishArtifact := false

releaseCrossBuild    := true
releaseTagComment    := s"Release ${(ThisBuild / version).value}"
releaseCommitMessage := s"Set version to ${(ThisBuild / version).value}"

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("+publishSigned"),
  releaseStepCommand("sonatypeBundleRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)

pomExtra := (<url>https://github.com/ua-parser/uap-scala</url>
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
