package uk.gov.hmrc.mobilehelptosave.domain

import cats.Id
import org.scalamock.scalatest.MockFactory
import org.scalatest._
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mobilehelptosave.support.{LoggerStub, ThrowableWithMessageContaining}

class TransactionsServiceSpec extends WordSpec
  with Matchers
  with MockFactory
  with OneInstancePerTest
  with LoggerStub
  with ThrowableWithMessageContaining
  with FutureAwaits
  with DefaultAwaitTimeout
  with OptionValues {



  "get some transactions" should {


    "get a thing" in {

      new TransactionsService[Id]((hc:HeaderCarrier) => (nino:String) => httpGet(nino))

    }
  }

}
