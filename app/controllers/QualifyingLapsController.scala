package controllers

import config.MyAppConfig.toleranceForLaps
import connectors.F1OpenApi
import models.{LapData, QualifyingLaps}
import play.api.libs.json.{JsArray, Json}
import play.api.mvc._
import services.Services.{convertToJsonArray, toMinutesAndSeconds}
import upickle.default._

import scala.concurrent.{ExecutionContext, Future}
import javax.inject._
import services.{MyLogger, Services}


@Singleton
class QualifyingLapsController @Inject()(val controllerComponents: ControllerComponents,
                                         val f1Api: F1OpenApi)(implicit executionContext: ExecutionContext) extends BaseController {

  private def sortAndFilterLaps(laps: List[QualifyingLaps]): List[QualifyingLaps] = {
    val sortedLaps = laps.sortBy(_.lap_duration.getOrElse(Double.MaxValue))
    val fastestLapOpt = sortedLaps.headOption.flatMap(_.lap_duration)

    fastestLapOpt match {
      case Some(fastestLap) =>
        val filteredLaps = sortedLaps.filter { lap =>
          lap.lap_duration.exists(_ <= fastestLap * toleranceForLaps
          )
        }
        filteredLaps.sortBy(_.lap_number)

      case None =>
        List.empty[QualifyingLaps]
    }
  }

  private def findAverageLaptime(laps: List[QualifyingLaps]): JsArray = {
    val average = laps.flatMap(_.lap_duration).sum / laps.length
    Json.arr(Json.obj("average_laptime" -> toMinutesAndSeconds(average)))
  }

  def findByDriverNumberAndSession: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val params: Map[String, String] = Services.extractParams(request).toMap
    val route = "/laps"

    val driverNumber = params.get("driver_number")
    val sessionKey = params.get("session_key")

    (driverNumber, sessionKey) match {
      case (Some(""), _ ) => Future.successful(BadRequest(s"Error with request, missing driver number"))
      case (_ , Some("")) => Future.successful(BadRequest(s"Error with request, missing session key"))

      case (Some(_), Some(_)) =>
        f1Api.lookup[List[QualifyingLaps]](route, params).map {
          case Right(listOfLaps) if listOfLaps.nonEmpty =>
            val hotLaps = sortAndFilterLaps(listOfLaps)
            val averageLap: JsArray = findAverageLaptime(hotLaps)
            if (hotLaps.isEmpty) {
              Ok(s"No completed laps for driver_number: ${driverNumber.get} and session_key:${sessionKey.get}")
            } else {
              implicit val ReadWriter: ReadWriter[LapData] = macroRW
              val laps = QualifyingLaps.toLapData(hotLaps)
              val jsonLaps = convertToJsonArray(laps)
              Ok(jsonLaps ++ averageLap)
            }

          case Right(_) =>
            NotFound("No qualifying laps found for the provided driver number and/or session key. " +
              "Please check and try again")

          case Left(errors) =>
            BadRequest(s"Error with request: $errors")
        }

      case _ =>
        Future.successful(BadRequest("Missing required parameters: 'driver_number' and/or 'session_key'"))
    }
  }

}
