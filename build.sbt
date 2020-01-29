scalaVersion := "2.13.1"

name := "wacc-compiler"
organization := "org.ic.wacc"
version := "1.0"

libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"

assemblyJarName in assembly := "compiler.jar"
test in assembly := {}
target in assembly := file("bin")
mainClass in assembly := Some("Main")
