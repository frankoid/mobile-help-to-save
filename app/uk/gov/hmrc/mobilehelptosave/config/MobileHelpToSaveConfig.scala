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

package uk.gov.hmrc.mobilehelptosave.config

import java.net.URL

import com.google.inject.ImplementedBy
import javax.inject.{Inject, Singleton}
import play.api.Configuration
import uk.gov.hmrc.mobilehelptosave.domain.Shuttering
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import scala.collection.JavaConverters._

@Singleton
case class MobileHelpToSaveConfig @Inject()(
  configuration: Configuration,
  servicesConfig: ServicesConfig
)
  extends AccountServiceConfig
    with DocumentationControllerConfig
    with HelpToSaveConnectorConfig
    with HelpToSaveControllerConfig
    with SandboxDataConfig
    with ServiceLocatorRegistrationTaskConfig
    with StartupControllerConfig {

  // These are eager vals so that missing or invalid configuration will be detected on startup
  override val helpToSaveBaseUrl: URL = configBaseUrl("help-to-save")

  override val serviceLocatorEnabled: Boolean = configBoolean("microservice.services.service-locator.enabled")

  override val shuttering: Shuttering = Shuttering(
    shuttered = configBoolean("helpToSave.shuttering.shuttered"),
    title = configBase64String("helpToSave.shuttering.title"),
    message = configBase64String("helpToSave.shuttering.message")
  )

  override val balanceEnabled: Boolean = configBoolean("helpToSave.balanceEnabled")
  override val paidInThisMonthEnabled: Boolean = configBoolean("helpToSave.paidInThisMonthEnabled")
  override val firstBonusEnabled: Boolean = configBoolean("helpToSave.firstBonusEnabled")
  override val shareInvitationEnabled: Boolean = configBoolean("helpToSave.shareInvitationEnabled")
  override val savingRemindersEnabled: Boolean = configBoolean("helpToSave.savingRemindersEnabled")
  override val transactionsEnabled: Boolean = configBoolean("helpToSave.transactionsEnabled")
  override val supportFormEnabled: Boolean = configBoolean("helpToSave.supportFormEnabled")
  override val inAppPaymentsEnabled: Boolean = configBoolean("helpToSave.inAppPaymentsEnabled")
  override val helpToSaveInfoUrl: String = configString("helpToSave.infoUrl")
  override val helpToSaveInvitationUrl: String = configString("helpToSave.invitationUrl")
  override val helpToSaveAccessAccountUrl: String = configString("helpToSave.accessAccountUrl")

  private val accessConfig = configuration.underlying.getConfig("api.access")
  override val apiAccessType: String = accessConfig.getString("type")
  override val apiWhiteListApplicationIds: Seq[String] = accessConfig.getStringList("white-list.applicationIds").asScala

  protected def configBaseUrl(serviceName: String): URL = new URL(servicesConfig.baseUrl(serviceName))

  private def configBoolean(path: String): Boolean = configuration.underlying.getBoolean(path)

  private def configLong(path: String): Long = configuration.underlying.getLong(path)

  private def configString(path: String): String = configuration.underlying.getString(path)

  private def configBase64String(path: String): String = {
    val encoded = configuration.underlying.getString(path)
    Base64.decode(encoded)
  }
}

@ImplementedBy(classOf[MobileHelpToSaveConfig])
trait AccountServiceConfig {
  def inAppPaymentsEnabled: Boolean
}

@ImplementedBy(classOf[MobileHelpToSaveConfig])
trait SandboxDataConfig {
  def inAppPaymentsEnabled: Boolean
}

@ImplementedBy(classOf[MobileHelpToSaveConfig])
trait DocumentationControllerConfig {
  def apiAccessType: String
  def apiWhiteListApplicationIds: Seq[String]
}

@ImplementedBy(classOf[MobileHelpToSaveConfig])
trait HelpToSaveConnectorConfig {
  def helpToSaveBaseUrl: URL
}

@ImplementedBy(classOf[MobileHelpToSaveConfig])
trait ServiceLocatorRegistrationTaskConfig {
  def serviceLocatorEnabled: Boolean
}

@ImplementedBy(classOf[MobileHelpToSaveConfig])
trait StartupControllerConfig {
  def shuttering: Shuttering
  def balanceEnabled: Boolean
  def paidInThisMonthEnabled: Boolean
  def firstBonusEnabled: Boolean
  def shareInvitationEnabled: Boolean
  def savingRemindersEnabled: Boolean
  def transactionsEnabled: Boolean
  def supportFormEnabled: Boolean
  def helpToSaveInfoUrl: String
  def helpToSaveInvitationUrl: String
  def helpToSaveAccessAccountUrl: String
}

@ImplementedBy(classOf[MobileHelpToSaveConfig])
trait HelpToSaveControllerConfig {
  def shuttering: Shuttering
}
