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

package uk.gov.hmrc.mobilehelptosave.services

import javax.inject.{Inject, Singleton}

import cats.data.OptionT
import cats.instances.future._
import com.google.inject.ImplementedBy
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mobilehelptosave.config.EnabledInvitationFilters

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[InvitationEligibilityServiceImpl])
trait InvitationEligibilityService {
  def userIsEligibleToBeInvited(nino: Nino)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[Boolean]]
}

@Singleton
class InvitationEligibilityServiceImpl @Inject() (
  surveyService: SurveyService,
  taxCreditsService: TaxCreditsService,
  enabledFilters: EnabledInvitationFilters
) extends InvitationEligibilityService {

  override def userIsEligibleToBeInvited(nino: Nino)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[Boolean]] = {
    val trueFO = Future successful Some(true)

    val surveyFO = if (enabledFilters.surveyInvitationFilter) surveyService.userWantsToBeContacted()
    else trueFO

    val wtcFO = if (enabledFilters.workingTaxCreditsInvitationFilter) taxCreditsService.hasRecentWtcPayments(nino)
    else trueFO

    (for {
      survey <- OptionT(surveyFO)
      wtc <- OptionT(wtcFO)
    } yield {
      survey && wtc
    }).value
  }
}