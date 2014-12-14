name := "todo-es"
 
version := "0.1"
 
scalaVersion := "2.11.4"

scalacOptions ++= Seq("-feature", "-deprecation", "-language:postfixOps", "-language:implicitConversions")

libraryDependencies ++= Seq(
    "com.firebase" % "firebase-client-jvm" % "2.0.2",
    "io.reactivex" %% "rxscala" % "0.22.0",
    "org.json4s" %% "json4s-jackson" % "3.2.11",
    "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
    "com.github.nscala-time" %% "nscala-time" % "1.6.0"
)