name := "sqlite4s"
version := "0.1"
scalaVersion := "2.11.12"

libraryDependencies += "biz.enef" %%% "slogging" % "0.6.1"
libraryDependencies += "biz.enef" % "slogging_native0.3_2.11" % "0.6.1"
libraryDependencies += "com.lihaoyi" %%% "utest" % "0.6.6" % "test"

testFrameworks += new TestFramework("utest.runner.Framework")

enablePlugins(ScalaNativePlugin)

// Set to false or remove if you want to show stubs as linking errors
nativeLinkStubs := true

nativeMode := "release"
//nativeLTO := "full" // will be available in 0.3.9
