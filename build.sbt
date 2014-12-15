

import Dependencies._

lazy val core = (project in file("core")).
                     settings(Commons.settings: _*).
                     settings(libraryDependencies ++= coreDependencies)

lazy val bdd = (project in file("bdd")).
                       settings(Commons.settings: _*).
                       settings(libraryDependencies ++= bddDependencies).
                       dependsOn(core)

lazy val example = (project in file("example")).
                  settings(Commons.settings: _*).
                  settings(libraryDependencies ++= exampleDependencies).
                  dependsOn(core, bdd)
