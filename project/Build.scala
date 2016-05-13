import play.sbt.PlayImport.PlayKeys
import sbt._
import Keys._
import play.Play.autoImport._
import PlayKeys._
import play.sbt.PlayScala
import play.sbt.routes.RoutesKeys._

object GotitBuild extends Build {
  lazy val core = Project("gotit-core", file("core"))
    .settings(
      libraryDependencies ++= Seq(
        "com.typesafe.slick" %% "slick" % "3.1.1",
        "mysql" % "mysql-connector-java" % "5.1.37",
        "org.scala-lang.modules" %% "scala-pickling" % "0.10.1")
    )

  lazy val interface = Project("gotit-interface", file("interface"))
      .dependsOn(core)

  lazy val frontend = Project("gotit-frontend", file("frontend"))
    .enablePlugins(PlayScala)
    .settings(
      libraryDependencies ++= Seq(
        filters,
        ws,
        evolutions,
        "com.typesafe.play" %% "play-slick" % "1.1.1",
        "com.typesafe.play" %% "play-slick-evolutions" % "1.1.1"
      ),
      routesGenerator := InjectedRoutesGenerator
    ).dependsOn(core)
}
