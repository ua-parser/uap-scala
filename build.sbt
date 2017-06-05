name := "uap-scala"

organization := "org.uaparser"

version := "0.1.0"

scalaVersion := "2.11.11"

crossScalaVersions := Seq("2.11.11", "2.12.2")

libraryDependencies ++= Seq(
  "org.yaml" % "snakeyaml" % "1.18",
  "com.twitter" %% "util-collection" % "6.43.0",
  "org.specs2" %% "specs2-core" % "2.4.17" % "test"
)

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
  </developers>)
