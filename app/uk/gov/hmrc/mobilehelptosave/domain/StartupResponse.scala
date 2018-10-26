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

package uk.gov.hmrc.mobilehelptosave.domain

import play.api.libs.json._

case class StartupResponse(
  shuttering: Shuttering,
  infoUrl: Option[String],
  invitationUrl: Option[String],
  accessAccountUrl: Option[String],
  user: Option[UserDetails],
  userError: Option[ErrorInfo],
  balanceEnabled: Boolean,
  paidInThisMonthEnabled: Boolean,
  firstBonusEnabled: Boolean,
  shareInvitationEnabled: Boolean,
  savingRemindersEnabled: Boolean,
  transactionsEnabled: Boolean,
  supportFormEnabled: Boolean,
  inAppPaymentsEnabled: Boolean
)

case class Shuttering (
  shuttered: Boolean,
  title: String,
  message: String
)

case object Shuttering {
  implicit val format: OFormat[Shuttering] = Json.format[Shuttering]
}

object StartupResponse {
  implicit val enabledWrites: Writes[StartupResponse] = Json.writes[StartupResponse]
    .transform((_: JsObject) + ("enabled" -> JsBoolean(true)))
}
