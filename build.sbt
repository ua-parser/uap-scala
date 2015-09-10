name := "uap-scala"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.5"

libraryDependencies ++= Seq(
  "org.yaml" % "snakeyaml" % "1.10",
  "com.twitter" %% "util-collection" % "6.23.0",
  "org.specs2" %% "specs2-core" % "2.4.15" % "test"
)

unmanagedResourceDirectories in Compile += baseDirectory.value / "core"

includeFilter in (Compile, unmanagedResources) := "regexes.yaml"

unmanagedResourceDirectories in Test += baseDirectory.value / "core"

includeFilter in (Test, unmanagedResources) := "*.yaml"
