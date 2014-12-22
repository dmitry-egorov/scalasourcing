import Dependencies._

lazy val core = (project in file("core")).
                settings(Commons.settings: _*).
                settings(libraryDependencies ++= coreDependencies)


lazy val example = (project in file("example")).
                   settings(Commons.settings: _*).
                   settings(libraryDependencies ++= exampleDependencies).
                   dependsOn(core)
