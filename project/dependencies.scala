import sbt._

object Dependencies
{
    val scalatest = "org.scalatest" % "scalatest_2.11" % "2.2.1"

    val coreDependencies: Seq[ModuleID] = Seq()
    val bddDependencies: Seq[ModuleID] = Seq()
    val exampleDependencies: Seq[ModuleID] = Seq(scalatest)
}