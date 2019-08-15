name := "uap-scala"
organization := "org.uaparser"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture"
)

scalacOptions := {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, scalaMajor)) if scalaMajor >= 11 =>
        scalacOptions.value :+ "-Xlint:adapted-args"
      case _ =>
        scalacOptions.value :+ "-Yno-adapted-args"
    }
  }

scalaVersion := "2.11.12"
crossScalaVersions := Seq("2.10.7", "2.11.12", "2.12.8", "2.13.0")

libraryDependencies +=  "org.yaml" % "snakeyaml" % "1.25"

libraryDependencies := {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, scalaMajor)) if scalaMajor >= 11 =>
      libraryDependencies.value ++ Seq("org.specs2" %% "specs2-core" % "4.5.1" % "test")
    case _ =>
      libraryDependencies.value ++ Seq("org.specs2" %% "specs2-core" % "3.10.0" % "test")
    }
  }

mimaPreviousArtifacts := Set("org.uaparser" %% "uap-scala" % "0.3.0")

unmanagedResourceDirectories in Compile += baseDirectory.value / "core"
includeFilter in (Compile, unmanagedResources) := "regexes.yaml"
unmanagedResourceDirectories in Test += baseDirectory.value / "core"
includeFilter in (Test, unmanagedResources) := "*.yaml"

// Publishing
publishMavenStyle := true
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}
publishArtifact in Test := false
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
