ThisBuild / version := "1.0.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.10"

lazy val root = (project in file("."))
  .settings(
    name := "spotify_scala"
  )


val sparkVersion = "3.5.0"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion

)

libraryDependencies += "com.softwaremill.sttp.client3" %% "core" % "3.3.15"
libraryDependencies += "com.softwaremill.sttp.client3" %% "async-http-client-backend-future" % "3.3.15"
libraryDependencies += "com.lihaoyi" %% "ujson" % "1.2.3"



