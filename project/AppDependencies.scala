import play.core.PlayVersion
import play.sbt.PlayImport.ws
import sbt.Keys._
import sbt.{ModuleID, _}

object AppDependencies {

  def appDependencies: Seq[Setting[_]] = Seq(
    libraryDependencies ++= compile ++ test ++ integrationTest,
    resolvers += "emueller-bintray" at "http://dl.bintray.com/emueller/maven"
  )

  private val bootstrapPlayVersion = "0.26.0"
  // This should be set to the version of Jetty that play-test depends on.
  // play-test 2.6.20 brings in jetty-http 9.4.5.v20170502
  // this is incompatible with jetty-server	9.2.24.v20180105 that is brought in by wiremock
  // so we override wiremock's dependencies with the later version
  private val jettyVersion = "9.4.5.v20170502"

  val compile = Seq(
    ws,
    "com.typesafe.play" %% "play-json-joda" % "2.6.10",
    "uk.gov.hmrc" %% "bootstrap-play-26" % bootstrapPlayVersion withSources(),
    "uk.gov.hmrc" %% "domain" % "5.2.0",
    "org.typelevel" %% "cats-core" % "1.1.0",
    "io.lemonlabs" %% "scala-uri" % "1.1.1",
    "uk.gov.hmrc" %% "play-hmrc-api" % "3.3.0-SNAPSHOT" //TODO release
  )

  val test: Seq[ModuleID] = testCommon("test") ++ Seq(
    "org.scalamock" %% "scalamock" % "4.0.0" % "test"
  )

  val integrationTest: Seq[ModuleID] = testCommon("it") ++ Seq (
    "com.github.tomakehurst" % "wiremock" % "2.19.0" % "it",
    "org.eclipse.jetty" % "jetty-server" % jettyVersion,
    "org.eclipse.jetty" % "jetty-servlet" % jettyVersion,
    "org.eclipse.jetty" % "jetty-servlets" % jettyVersion,
    "org.eclipse.jetty" % "jetty-webapp" % jettyVersion
  )

  def testCommon(scope: String) = Seq(
    "org.scalatest" %% "scalatest" % "3.0.5" % scope,
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % scope,
    "org.pegdown" % "pegdown" % "1.6.0" % scope,
    "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
    "com.eclipsesource" %% "play-json-schema-validator" % "0.9.4" % scope
  )
}
