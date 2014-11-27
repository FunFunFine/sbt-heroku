import NativePackagerKeys._

packageArchetype.java_application

name := """scala-getting-started"""

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "com.twitter" % "finagle-http_2.10" % "6.18.0"
)

lazy val remoteAppName = "sbt-heroku-" + sys.props("heroku.uuid")

herokuJdkVersion in Compile := "1.7"

herokuAppName in Compile := remoteAppName

herokuStack in Compile := "cedar-14"

herokuConfigVars in Compile := Map(
  "MY_VAR" -> "monkeys with a y",
  "JAVA_OPTS" -> "-Xmx384m -Xss512k -DmyVar=monkeys"
)

TaskKey[Unit]("createApp") <<= (packageBin in Universal, streams) map { (zipFile, streams) =>
  Process("heroku", Seq("apps:destroy", "-a", remoteAppName, "--confirm", remoteAppName)) ! streams.log
  Process("heroku", Seq("create", "-n", remoteAppName)) ! streams.log
}

TaskKey[Unit]("cleanup") <<= (packageBin in Universal, streams) map { (zipFile, streams) =>
  Process("heroku", Seq("apps:destroy", "-a", remoteAppName, "--confirm", remoteAppName)) ! streams.log
}

TaskKey[Unit]("check") <<= (packageBin in Universal, streams) map { (zipFile, streams) =>
  val config = Process("heroku", Seq("config", "-a", remoteAppName)).!!
  if (!(config.contains("MY_VAR") && config.contains("monkeys with a y"))) {
    sys.error("Custom config variable was not set!")
  }
  if (!(config.contains("JAVA_OPTS") && config.contains("-Xmx384m -Xss512k -DmyVar=monkeys"))) {
    sys.error("Default config variable was not overridden!")
  }
  if (!(config.contains("PATH") && config.contains(".jdk/bin:/usr/local/bin:/usr/bin:/bin"))) {
    sys.error("Default config variable was not retained!")
  }
  val info = Process("heroku", Seq("apps:info", "-a", remoteAppName)).!!
  if (!info.contains("cedar-14")) {
    sys.error("Custom config variable was not set!")
  }
}