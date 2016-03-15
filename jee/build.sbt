scalaVersion := "2.11.7"
name := "wbport"
version := "0.20160308"

enablePlugins(JettyPlugin)

libraryDependencies += "org.scala-lang" % "scala-compiler" % "2.11.7"
libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.1"
libraryDependencies += "org.flywaydb" % "flyway-core" % "3.2.1"
libraryDependencies += "com.h2database" % "h2" % "1.4.191"
libraryDependencies += "org.scalikejdbc" % "scalikejdbc_2.11" % "2.3.5"
libraryDependencies += "commons-io" % "commons-io" % "2.4"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.38"
libraryDependencies += "joda-time" % "joda-time" % "2.9.2"
libraryDependencies += "org.scalatra" % "scalatra_2.11" % "2.4.0"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.5"
libraryDependencies += "com.typesafe.scala-logging" % "scala-logging-slf4j_2.11" % "2.1.2"
libraryDependencies += "org.slf4j" % "jcl-over-slf4j" % "1.7.16"

libraryDependencies += "org.jsoup" % "jsoup" % "1.8.3"
libraryDependencies += "com.mashape.unirest" % "unirest-java" % "1.4.8"

libraryDependencies ++= Seq(
  "poi", "poi-ooxml"
).map("org.apache.poi" % _ % "3.14")

libraryDependencies ++= Seq(
  "jetty-webapp","jetty-plus"
).map("org.eclipse.jetty" % _ % "9.2.14.v20151106")

assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".properties" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".xml" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".types" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".class" => MergeStrategy.first
  case "application.conf"                            => MergeStrategy.concat
  case "unwanted.txt"                                => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

lazy val root = (project in file(".")).
  enablePlugins(BuildInfoPlugin).
  settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion) //,
    //buildInfoPackage := "buildinfo"
  )
