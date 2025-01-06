package controllers

import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import javax.inject._
import services.{LapsService, Utilities}

@Singleton
class LapsController @Inject()(val controllerComponents: ControllerComponents, LapService: LapsService)(implicit executionContext: ExecutionContext) extends BaseController {

  def findByDriverNumberAndSession: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val params: Map[String, String] = Utilities.extractParams(request).toMap
    val driverNumber = params.get("driver_number")
    val sessionKey = params.get("session_key")

    (driverNumber, sessionKey) match {
      case (Some(""), _ ) => Future.successful(BadRequest(s"Error with request, missing driver number"))
      case (_ , Some("")) => Future.successful(BadRequest(s"Error with request, missing session key"))

      case (Some(_), Some(_)) =>
        LapService.findByDriverAndSession(params).map { lapDataList =>
          if (lapDataList.nonEmpty) {
            val response = LapService.findAverageLaptime(lapDataList)

            Ok(response)
          } else {
            NoContent
          }
        }.recover {
          case ex: Exception =>
            InternalServerError(Json.obj("error" -> ex.getMessage))
        }

      case _ =>
        Future.successful(BadRequest("Missing required parameters: 'driver_number' and/or 'session_key'"))
    }
  }
}
