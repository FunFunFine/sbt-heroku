import bintray.Keys._

sbtPlugin := true

name := "sbt-heroku"

organization := "com.heroku"

scalaVersion in Global := "2.10.4"

scalacOptions in Compile += "-deprecation"

resolvers += Resolver.bintrayRepo("heroku", "maven")

libraryDependencies ++= Seq(
  "com.heroku.sdk" % "heroku-deploy" % "1.0.0"
)

scriptedSettings

scriptedLaunchOpts += { "-Dproject.version="+version.value }

scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-XX:MaxPermSize=256M",
    "-Dheroku.uuid=" + java.util.UUID.randomUUID.toString.substring(0,15))
}

publishMavenStyle := false

bintrayPublishSettings

repository in bintray := "sbt-plugins"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

bintrayOrganization in bintray := Some("heroku")

Release.settings
