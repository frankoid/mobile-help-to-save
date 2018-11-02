/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.mobilehelptosave

import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.{PortNumber, WsScalaTestClient}
import play.api.Application
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.domain.Generator
import uk.gov.hmrc.mobilehelptosave.stubs.{AuthStub, HelpToSaveStub}
import uk.gov.hmrc.mobilehelptosave.support.{JsonMatchers, WireMockSupport, WithTestServer}

/**
  * Tests that the Get Account endpoint uses configuration values correctly
  * (e.g. changes its response when configuration is changed).
  */
class AccountConfigISpec extends WordSpec with Matchers with JsonMatchers with FutureAwaits with DefaultAwaitTimeout
  with WsScalaTestClient with WireMockSupport with WithTestServer {

  private val generator = new Generator(0)
  private val nino = generator.nextNino

  "GET /savings-account/{nino} and /sandbox/savings-account/{nino}" should {
    "allow inAppPaymentsEnabled to be overridden with configuration" in {

      AuthStub.userIsLoggedIn(nino)
      HelpToSaveStub.currentUserIsEnrolled()
      HelpToSaveStub.accountExists(nino)

      withTestServer(
        appBuilder
          .configure(
            "helpToSave.inAppPaymentsEnabled" -> "false"
          )
          .build()) {

        (app: Application, portNumber: PortNumber) =>
        implicit val implicitPortNumber: PortNumber = portNumber
        implicit val wsClient: WSClient = app.injector.instanceOf[WSClient]

        responseShouldHaveInAppPaymentsEqualTo(await(wsUrl(s"/savings-account/$nino").get()), expectedValue = false)
        responseShouldHaveInAppPaymentsEqualTo(await(wsUrl(s"/sandbox/savings-account/$nino").get()), expectedValue = false)
      }

      withTestServer(
        appBuilder
          .configure(
            "helpToSave.inAppPaymentsEnabled" -> "true"
          )
          .build()) { (app: Application, portNumber: PortNumber) =>
        implicit val implicitPortNumber: PortNumber = portNumber
        implicit val wsClient: WSClient = app.injector.instanceOf[WSClient]

        responseShouldHaveInAppPaymentsEqualTo(await(wsUrl(s"/savings-account/$nino").get()), expectedValue = true)
        responseShouldHaveInAppPaymentsEqualTo(await(wsUrl(s"/sandbox/savings-account/$nino").get()), expectedValue = true)
      }
    }
  }

  private def responseShouldHaveInAppPaymentsEqualTo(response: WSResponse, expectedValue: Boolean) = {
    response.status shouldBe 200

    (response.json \ "inAppPaymentsEnabled").as[Boolean] shouldBe expectedValue
  }

}