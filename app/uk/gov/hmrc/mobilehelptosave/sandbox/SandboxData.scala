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

package uk.gov.hmrc.mobilehelptosave.sandbox


import javax.inject.{Inject, Singleton}
import org.joda.time.{LocalDate, YearMonth}
import play.api.LoggerLike
import uk.gov.hmrc.mobilehelptosave.config.SandboxDataConfig
import uk.gov.hmrc.mobilehelptosave.connectors.{HelpToSaveAccount, HelpToSaveBonusTerm}
import uk.gov.hmrc.mobilehelptosave.domain._
import uk.gov.hmrc.mobilehelptosave.services.Clock


@Singleton
case class SandboxData @Inject()
(
  logger: LoggerLike,
  clock: Clock,
  config: SandboxDataConfig
) {

  private def today: LocalDate = clock.now().toLocalDate
  private def openedDate: LocalDate = today.minusMonths(7)
  private def endOfMonth: LocalDate = today.dayOfMonth.withMaximumValue
  private def startOfFirstTerm: LocalDate = openedDate.dayOfMonth.withMinimumValue
  private def endOfFirstTerm: LocalDate = startOfFirstTerm.plusYears(2).minusDays(1)
  private def startOfSecondTerm: LocalDate = startOfFirstTerm.plusYears(2)
  private def endOfSecondTerm: LocalDate = startOfSecondTerm.plusYears(2).minusDays(1)

  val account: Account = {
    Account(
      HelpToSaveAccount(
        accountNumber = "1100000112057",
        openedYearMonth = new YearMonth(openedDate.getYear, openedDate.getMonthOfYear),
        isClosed = false,
        blocked = Blocking(false),
        balance = BigDecimal("220.50"),
        paidInThisMonth = BigDecimal("20.50"),
        canPayInThisMonth = BigDecimal("29.50"),
        maximumPaidInThisMonth = BigDecimal("50.00"),
        thisMonthEndDate = endOfMonth,
        accountHolderForename = "Testfore",
        accountHolderSurname = "Testsur",
        accountHolderEmail = Some("testemail@example.com"),
        bonusTerms = List(
          HelpToSaveBonusTerm(BigDecimal("110.25"), BigDecimal("0.00"), endOfFirstTerm, endOfFirstTerm.plusDays(1)),
          HelpToSaveBonusTerm(BigDecimal("0.00"), BigDecimal("0.00"), endOfSecondTerm, endOfSecondTerm.plusDays(1))
        ),
        closureDate = None,
        closingBalance = None
      ),
      inAppPaymentsEnabled = config.inAppPaymentsEnabled,
      logger)
  }

  val transactions = Transactions({
    Seq(
      (0, 20.50, 220.50),
      (1, 20.00, 200.00),
      (1, 18.20, 180.00),
      (2, 10.40, 161.80),
      (3, 35.00, 151.40),
      (3, 15.00, 116.40),
      (4, 06.00, 101.40),
      (5, 20.40, 95.40),
      (5, 10.00, 75.00),
      (6, 25.00, 65.00),
      (7, 40.00, 40.00)
    ) map { case (monthsAgo, creditAmount, balance) =>
      val date = today.minusMonths(monthsAgo)
      Transaction(Credit, BigDecimal(creditAmount), date, date, BigDecimal(balance))
    }
  })
}
