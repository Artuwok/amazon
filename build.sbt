name := "amazon"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.1.0"
libraryDependencies += "org.apache.spark" % "spark-sql_2.11" % "2.1.0"
libraryDependencies += "com.databricks" % "spark-csv_2.11" % "1.5.0"
libraryDependencies += "net.databinder.dispatch" % "dispatch-core_2.11" % "0.12.0"
libraryDependencies += "io.argonaut" %% "argonaut" % "6.1"
