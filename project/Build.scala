import sbt._
import Keys._

object ThisBuild extends Build {

  val SNAPSHOT = "-SNAPSHOT"

  lazy val buildVersion =  "0.0.1" + SNAPSHOT

  val neo4jVersion                  = "1.9.M03"
  lazy val neo4jServer              = "org.neo4j.app" % "neo4j-server" % neo4jVersion classifier "static-web"  classifier "tests" classifier ""
  lazy val neo4jkernel              = "org.neo4j" % "neo4j-kernel" % neo4jVersion  classifier "tests" classifier ""
  lazy val jerseyForNeo4J           = "com.sun.jersey" % "jersey-core" % "1.9"

  val libDependencies = Seq(
    neo4jServer,
    neo4jkernel,
    jerseyForNeo4J
  )


  lazy val root = {
    Project(
      id = "sbt-neo4j-test-plugin",
      base = file("."),
      settings = Project.defaultSettings ++ net.virtualvoid.sbt.graph.Plugin.graphSettings
    ).settings(
      scalaVersion := "2.10.0",
      sbtPlugin := true,
      version := buildVersion,
      organization := "be.nextlab",
      //resolvers ++= Seq(typesafe, typesafeSnapshot),
      javacOptions += "-Xlint:unchecked",
      libraryDependencies ++= libDependencies,
      publishMavenStyle := true,

      licenses := Seq("Apache License, Version 2.0" -> url("http://opensource.org/licenses/apache2.0.php")),

      publishMavenStyle := true,
      publishTo <<= version { (v: String) =>
                      val nexus = "https://oss.sonatype.org/"
                      if (v.trim.endsWith("SNAPSHOT"))
                        Some("snapshots" at nexus + "content/repositories/snapshots")
                      else
                        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
                    },

      credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),

      publishArtifact in Test := false,
      pomIncludeRepository := { _ => false },

      pomExtra := (
        <url>https://github.com/andypetrella/sbt-neo4j-test-plugin</url>
        <scm>
          <url>git@github.com:andypetrella/sbt-neo4j-test-plugin.git</url>
          <connection>scm:git:git@github.com:andypetrella/sbt-neo4j-test-plugin.git</connection>
        </scm>
        <developers>
          <developer>
            <id>noootsab</id>
            <name>Andy Petrella</name>
            <url>http://ska-la.blogspot.be/</url>
          </developer>
        </developers>)
    )
  }

}