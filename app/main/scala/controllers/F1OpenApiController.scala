package main.scala.controllers

import main.scala.config.MyAppConfig
import play.api.libs.json.{JsError, JsSuccess, Json, Reads, __}
import play.api.mvc.{BaseController, ControllerComponents}

import com.google.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class F1OpenApiController @Inject()(implicit executionContext: ExecutionContext,
                                val controllerComponents: ControllerComponents,
                                config: MyAppConfig) extends BaseController {

  def lookup[T: Reads](route: String, params: Iterable[(String, String)]): Future[Either[String, T]] = {
    val response = requests.get(s"${config.apiBaseUrl}$route", params = params)
    val json = Json.parse(response.data.array)

    json.validate[T] match {
      case JsSuccess(data, _) => Future.successful(Right(data))
      case JsError(errors) => Future.successful(Left(errors.mkString(", ")))
    }

  }






}