name := "TOFU101"

version := "0.1"

scalaVersion := "2.12.12"

val tofuVersion = "0.9.0"
val catsVersion = "2.2.0"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-effect" % catsVersion,
  "ru.tinkoff" %% "tofu-core" % tofuVersion,
  "ru.tinkoff" %% "tofu-logging" % tofuVersion,
  "ru.tinkoff" %% "tofu-optics-macro" % tofuVersion,
  "ru.tinkoff" %% "tofu-data" % tofuVersion,
  "ru.tinkoff" %% "tofu-derivation" % tofuVersion,
  "ru.tinkoff" %% "tofu-doobie" % tofuVersion,
  "io.janstenpickle" %% "trace4cats-core" % "0.8.0",
  "io.janstenpickle" %% "trace4cats-inject" % "0.8.0",
  "org.tpolecat" %% "natchez-jaeger" % "0.0.18"
)

addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
)

addCompilerPlugin(
  "org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full
)

addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
