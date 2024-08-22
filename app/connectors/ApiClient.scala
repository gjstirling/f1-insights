package connectors

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import config.MyAppConfig._

trait ApiClient {
  def get(route: String, params: Iterable[(String, String)]): Future[Either[String, Array[Byte]]]
}

class RealApiClient @Inject ()(implicit ec: ExecutionContext) extends ApiClient {
  override def get(route: String, params: Iterable[(String, String)]): Future[Either[String, Array[Byte]]] = Future {
    try {
      val response = requests.get(s"$apiBaseUrl$route", params = params)
      Right(response.data.array)
    } catch {
      case ex: Exception => Left(ex.getMessage)
    }
  }
}