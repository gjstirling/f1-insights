package connectors

import com.google.inject.{Inject, Singleton}
import play.api.libs.json.{JsError, JsSuccess, Json, Reads}
import services.MyLogger

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class F1OpenApi @Inject()(apiClient: ApiClient)
                                   (implicit executionContext: ExecutionContext) {

  def lookup[T: Reads](route: String, params: Iterable[(String, String)]): Future[Either[String, T]] = {
    apiClient.get(route, params).map {
      case Right(data) =>
        val json = Json.parse(data)
        json.validate[T] match {
          case JsSuccess(value, _) => Right(value)
          case JsError(errors) =>
            MyLogger.red("lookup: Right Error with Validation")
            Left(errors.mkString(", "))
        }
      case Left(error) =>
        MyLogger.red("lookup: Left Error type String")
        Left(error)
    }
  }
}
