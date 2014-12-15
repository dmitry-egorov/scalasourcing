import sbt.Keys._
import sbt._

object Commons
{
    val settings: Seq[Def.Setting[_]] = Seq(
        version := "0.1",
        scalaVersion := "2.11.4",
        scalacOptions := Seq("-feature", "-deprecation", "-language:postfixOps", "-language:implicitConversions")
    )
}