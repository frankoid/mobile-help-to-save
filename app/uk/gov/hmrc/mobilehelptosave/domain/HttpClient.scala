package uk.gov.hmrc.mobilehelptosave.domain

import cats.Monad
import com.google.inject.Inject
import play.api.libs.ws.WSClient
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.ExecutionContext


object HttpClient {

  type HttpGet = HeaderCarrier => String => HttpResponse

  case class HttpResponse(r:)
}


class HttpClient @Inject()(ws:WSClient) {

  def httpGet[A](url:String)(implicit ec:ExecutionContext) = {
    ws.url(url)
      .get()
      .map(r => HttpResponse(r.status, Some(r.json)))
  }
}


class TransactionsService[F[_]:Monad]( getRequest: HttpClient.HttpGet => F[String] ) {

  def getTransactions(nino:String) : F[String] = {
    getRequest(s"localhost:8000/account/$nino/transactions")
  }
}
