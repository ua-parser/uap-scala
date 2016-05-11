name := "uap-scala"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.yaml" % "snakeyaml" % "1.17",
  "com.twitter" %% "util-collection" % "6.34.0",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "org.scalacheck" %% "scalacheck" % "1.13.1" % "test",
  "org.specs2" %% "specs2-core" % "2.4.15" % "test"
)

unmanagedResourceDirectories in Compile += baseDirectory.value / "core"

includeFilter in (Compile, unmanagedResources) := "regexes.yaml"

unmanagedResourceDirectories in Test += baseDirectory.value / "core"

includeFilter in (Test, unmanagedResources) := "*.yaml"
