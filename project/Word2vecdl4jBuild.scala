import sbt._
import sbt.Keys._

object Glove4jBuild extends Build {

  lazy val Glove4j = Project(
    id = "Glove4j",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "Glove4j",
      organization := "com.pyaanalytics",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.10.4",

      libraryDependencies ++= Seq(
        "com.github.scopt" %% "scopt" % "3.2.0",
        "org.nd4j" % "nd4j-jcublas-6.0" % "0.0.3.5.5.3-SNAPSHOT",
        "org.nd4j" % "nd4j-api" % "0.0.3.5.5.3-SNAPSHOT",
        "org.deeplearning4j" % "deeplearning4j-nlp" % "0.0.3.3.3.alpha1-SNAPSHOT"
          exclude("javax.jms", "jms")
          exclude("com.sun.jdmk", "jmxtools")
          exclude("com.sun.jmx", "jmxri")
      ),

      resolvers ++= Seq(
        "JBoss" at "https://repository.jboss.org/nexus/content/groups/public",
        "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
        // "Nexus Release Repository" at "http://oss.sonatype.org/service/local/staging/deploy/maven2/",
        Resolver.mavenLocal
      )
      // add other settings here
    )
  )
}
