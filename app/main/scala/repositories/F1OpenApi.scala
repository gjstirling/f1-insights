package main.scala.repositories

import com.google.inject.{Inject, Singleton}
import play.api.libs.json.{JsError, JsSuccess, Json, Reads}
import services.ApiClient

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
          case JsError(errors) => Left(errors.mkString(", "))
        }
      case Left(error) => Left(error)
    }
  }
}
