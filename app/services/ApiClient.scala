package services

import main.scala.config.MyAppConfig
import scala.concurrent.{ExecutionContext, Future}
import javax.inject.Inject

trait ApiClient {
  def get(route: String, params: Iterable[(String, String)]): Future[Either[String, Array[Byte]]]
}

class RealApiClient @Inject()(config: MyAppConfig)(implicit ec: ExecutionContext) extends ApiClient {
  override def get(route: String, params: Iterable[(String, String)]): Future[Either[String, Array[Byte]]] = Future {
    try {
      val response = requests.get(s"${config.apiBaseUrl}$route", params = params)
      Right(response.data.array)
    } catch {
      case ex: Exception => Left(ex.getMessage)
    }
  }
}