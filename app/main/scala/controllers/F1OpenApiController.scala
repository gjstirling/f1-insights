package main.scala.controllers

import play.api.libs.json.{JsError, JsSuccess, Json, Reads}
import play.api.mvc.{BaseController, ControllerComponents}
import com.google.inject.{Inject, Singleton}
import services.ApiClient
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class F1OpenApiController @Inject()(val controllerComponents: ControllerComponents, apiClient: ApiClient)
                                   (implicit executionContext: ExecutionContext) extends BaseController {

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
