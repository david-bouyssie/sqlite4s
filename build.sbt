name := "sqlite4s"
organization := "com.github.david-bouyssie"
version := "0.3.0"
scalaVersion := "2.11.12"
crossScalaVersions := Seq("2.13.4", "2.12.13", "2.11.12")

libraryDependencies += "com.outr" %%% "scribe" % "3.3.3"
libraryDependencies += "com.lihaoyi" %%% "utest" % "0.7.7" % "test"

testFrameworks += new TestFramework("utest.runner.Framework")

enablePlugins(ScalaNativePlugin)

// Set to false or remove if you want to show stubs as linking errors
nativeLinkStubs := true

nativeMode := "release"
nativeLTO := "thin"

// Your profile name of the sonatype account. The default is the same with the organization value
//sonatypeProfileName := "david-bouyssie"

scmInfo := Some(
  ScmInfo(
    url("https://github.com/david-bouyssie/sqlite4s"),
    "scm:git@github.com:david-bouyssie/sqlite4s.git"
  )
)
developers := List(
  Developer(
    id    = "david-bouyssie",
    name  = "David Bouyssié",
    email = "",
    url   = url("https://github.com/david-bouyssie")
  )
)
description := "A Scala Native wrapper of the SQLite C library."
licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
homepage := Some(url("https://github.com/david-bouyssie/sqlite4s"))
pomIncludeRepository := { _ => false }
publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

// Workaround for issue https://github.com/sbt/sbt/issues/3570 (fixed in 1.3.x)
//updateOptions := updateOptions.value.withGigahorse(false)

// To publish to central:
// export GPG_TTY=$(tty) # gpg issue (https://github.com/keybase/keybase-issues/issues/2798)
// sbt publishSigned
//useGpg := true // (since 2.0.0): useGpg is true by default
//pgpPublicRing := file("~/.gnupg/pubring.kbx")
//pgpSecretRing := file("~/.gnupg/pubring.kbx")
Test / skip in publish := true

/*
val commonSettings = Seq(
  version := "0.2.0",
  organization := "com.github.david-bouyssie",
  scalaVersion := "2.11.12",
  //nativeLinkStubs := true,
  nativeMode := "release",
  libraryDependencies ++= Seq(
    "biz.enef" %%% "slogging" % "0.6.1",
    //"biz.enef" % "slogging_native0.3_2.11" % "0.6.1",
    "com.lihaoyi" %%% "utest" % "0.6.6" % "test"
  ),
  Test / nativeLinkStubs := true
)

val publishSettings = Seq(
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/david-bouyssie/sqlite4s"),
      "scm:git@github.com:david-bouyssie/sqlite4s.git"
    )
  ),
  developers := List(
    Developer(
      id    = "david-bouyssie",
      name  = "David Bouyssié",
      email = "",
      url   = url("https://github.com/david-bouyssie")
    )
  ),
  description := "A Scala Native wrapper of the SQLite C library.",
  licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")),
  homepage := Some(url("https://github.com/david-bouyssie/sqlite4s")),
  pomIncludeRepository := { _ => false },
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  publishMavenStyle := true,
  useGpg := true,
  Test / skip in publish := true
)

val sqlite4s = project
  .settings(commonSettings)
  .settings(publishSettings)
  .enablePlugins(ScalaNativePlugin)
*/