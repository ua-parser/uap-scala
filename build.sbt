name := "uap-scala"

lazy val compilerOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-unchecked",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture",
  "-Yinline-warnings",
  "-Xlint"
)

val snakeyamlVersion = "1.17"
val twitterUtilVersion = "6.34.0"
val scalaCheckVersion = "1.13.1"
val scalatestVersion = "2.2.6"
val specs2Version = "2.4.15"

lazy val coreDeps = Seq(
  "org.yaml" % "snakeyaml" % snakeyamlVersion,
  "com.twitter" %% "util-collection" % twitterUtilVersion
)

lazy val testDeps = Seq(
  "org.scalatest" %% "scalatest" % scalatestVersion,
  "org.scalacheck" %% "scalacheck" % scalaCheckVersion,
  "org.specs2" %% "specs2-core" % specs2Version
) map (_ % "test")

lazy val buildSettings = Seq(
  organization := "com.github.yanana",
  scalaVersion := "2.11.8"
)

lazy val baseSettings = Seq(
  scalacOptions ++= compilerOptions,
  scalacOptions in (Compile, console) := compilerOptions,
  scalacOptions in (Compile, test) := compilerOptions,
  libraryDependencies ++= coreDeps ++ testDeps,
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)

libraryDependencies ++= coreDeps ++ testDeps

unmanagedResourceDirectories in Compile += baseDirectory.value / "core"
includeFilter in (Compile, unmanagedResources) := "regexes.yaml"
unmanagedResourceDirectories in Test += baseDirectory.value / "core"
includeFilter in (Test, unmanagedResources) := "*.yaml"

scalacOptions ++= compilerOptions

lazy val publishSettings = Seq(
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  homepage := Some(url("https://github.com/yanana/uap-scala")),
  licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.php")),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/yanana/uap-scala"),
      "scm:git:git@github.com:yanana/uap-scala.git"
    )
  ),
  pomExtra :=
    <developers>
      <developer>
        <id>yanana</id>
        <name>Shun Yanaura</name>
        <url>https://github.com/yanana</url>
      </developer>
    </developers>
)

lazy val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)

lazy val allSettings = buildSettings ++ baseSettings ++ publishSettings ++ scalariformSettings

lazy val root = (project in file("."))
  .settings(
    name := "uap-scala",
    description := "ua-parser library for Scala",
    moduleName := "uap-scala"
  )
  .settings(allSettings: _*)
