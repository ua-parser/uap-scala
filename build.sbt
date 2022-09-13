name := "uap-scala"
organization := "org.uaparser"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked"
)

val scala2Flags = Seq(
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture"
)

scalacOptions := {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((3, _)) =>
        scalacOptions.value :+ "-language:implicitConversions"
      case Some((2, scalaMajor)) if scalaMajor >= 11 =>
        scalacOptions.value ++ scala2Flags :+ "-Xlint:adapted-args"
      case _ =>
        scalacOptions.value ++ scala2Flags :+ "-Yno-adapted-args"
    }
}

scalaVersion := "2.13.8"
crossScalaVersions := Seq("2.10.7", "2.11.12", "2.12.15", "2.13.8", "3.1.1")

libraryDependencies +=  "org.yaml" % "snakeyaml" % "1.32"

libraryDependencies := {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((3, _)) =>
      libraryDependencies.value ++ Seq("org.specs2" %% "specs2-core" % "5.0.0-RC-22" % "test")
    case Some((2, scalaMajor)) if scalaMajor >= 11 =>
      libraryDependencies.value ++ Seq("org.specs2" %% "specs2-core" % "4.10.6" % "test")
    case _ =>
      libraryDependencies.value ++ Seq("org.specs2" %% "specs2-core" % "3.10.0" % "test")
    }
  }

coverageEnabled := {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, scalaMajor)) if scalaMajor <= 11  => false // The 2.x version doesn't support Scala 2.10 and 2.11
    case _                                          => true
  }
}

mimaPreviousArtifacts := Set("org.uaparser" %% "uap-scala" % "0.3.0")

Compile / unmanagedResourceDirectories += baseDirectory.value / "core"
Compile / unmanagedResources / includeFilter := "regexes.yaml"
Test / unmanagedResourceDirectories += baseDirectory.value / "core"
Test / unmanagedResources / includeFilter := "*.yaml"

// Publishing
publishMavenStyle := true
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}
Test / publishArtifact := false
releaseCrossBuild := true
releasePublishArtifactsAction := PgpKeys.publishSigned.value

pomExtra := (
  <url>https://github.com/ua-parser/uap-scala</url>
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
