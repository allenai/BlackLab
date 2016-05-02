import sbt._
import org.allenai.plugins.CoreDependencies
import sbtrelease._
import sbtrelease.ReleaseStateTransformations._

name := "blacklab"

libraryDependencies ++= Seq(
  "org.scalacheck" %% "scalacheck" % "1.12.0" % Test,
  "com.novocode" % "junit-interface" % "0.11" % Test,
  "org.apache.lucene" % "lucene-core" % "4.2.1",
  "org.apache.lucene" % "lucene-queryparser" % "4.2.1",
  "org.apache.lucene" % "lucene-highlighter" % "4.2.1",
  "org.apache.lucene" % "lucene-queries" % "4.2.1",
  "org.apache.lucene" % "lucene-analyzers-common" % "4.2.1",
  "tomcat" % "jasper-runtime" % "5.5.23",
  "tomcat" % "jsp-api" % "5.5.23",
  "tomcat" % "servlet-api" % "5.5.23",
  "com.goldmansachs" % "gs-collections" % "6.1.0",
//  Logging.logbackClassic,
//  Logging.logbackCore,
//  Logging.slf4jApi,
//  "org.slf4j" % "log4j-over-slf4j" % Logging.slf4jVersion)
  "org.slf4j" % "jcl-over-slf4j" % "1.7.7")

// Override the problematic new release plugin.
lazy val releaseProcessSetting = releaseProcess := Seq(
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  publishArtifacts,
  setNextVersion,
  commitNextVersion,
  pushChanges
)


lazy val buildSettings = Seq(
  organization := "nl.inl.blacklab",
  scalaVersion <<= crossScalaVersions { (vs: Seq[String]) => vs.head },
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html")),
  homepage := Some(url("https://github.com/allenai/BlackLab")),
  scmInfo := Some(ScmInfo(
    url("https://github.com/allenai/BlackLab"),
    "https://github.com/allenai/BlackLab.git")),
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  bintrayPackage := s"${organization.value}:${name.value}_${scalaBinaryVersion.value}",
  pomExtra :=
    <developers>
      <developer>
        <id>allenai-dev-role</id>
        <name>Allen Institute for Artificial Intelligence</name>
        <email>dev-role@allenai.org</email>
      </developer>
    </developers>)

releaseVersion := { ver =>
  val snapshot = "(.*-ALLENAI-\\d+)".r
  ver match {
    case snapshot(v) => v
    case _ => versionFormatError
  }
}

releaseNextVersion := { ver =>
  val release = "(.*-ALLENAI)-(\\d+)".r
  // pattern matching on Int
  object Int {
    def unapply(s: String): Option[Int] = try {
      Some(s.toInt)
    } catch {
      case _: java.lang.NumberFormatException => None
    }
  }

  ver match {
    case release(prefix, Int(number)) => s"$prefix-${number+1}-SNAPSHOT"
    case _ => versionFormatError
  }
}

javacOptions in doc := Seq(
  "-source", "1.7",
  "-group", "Core", "nl.inl.blacklab.search:nl.inl.blacklab.search.*:nl.inl.blacklab.tools:nl.inl.blacklab.index:nl.inl.blacklab.index.*:nl.inl.blacklab.highlight:nl.inl.blacklab.queryParser.*:nl.inl.blacklab.perdocument",
  "-group", "Examples and tests", "nl.inl.blacklab.example:nl.inl.blacklab.indexers.*",
  "-group", "Supporting classes", "nl.inl.blacklab.filter:nl.inl.blacklab.forwardindex:nl.inl.blacklab.externalstorage:nl.inl.blacklab.suggest:nl.inl.util",
  "-Xdoclint:none")


lazy val blacklabRoot = Project(
  id = "blacklabRoot",
  base = file("."),
  settings = buildSettings ++ releaseProcessSetting
).enablePlugins(LibraryPlugin)

