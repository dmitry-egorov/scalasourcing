import sbt._

object Dependencies
{
    val scalatest = "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"

    val coreDependencies: Seq[ModuleID] = Seq(scalatest)
    val exampleDependencies: Seq[ModuleID] = Seq(scalatest)
}