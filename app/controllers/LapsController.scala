package controllers

import config.MyAppConfig.toleranceForLaps
import connectors.F1OpenApi
import models.{LapData, Laps}
import play.api.libs.json.{JsArray, Json}
import play.api.mvc._
import repositories.LapsRepository
import services.Utilities.{convertToJsonArray, toMinutesAndSeconds}
import upickle.default._

import scala.concurrent.{ExecutionContext, Future}
import javax.inject._
import services.{MyLogger, Utilities}


@Singleton
class LapsController @Inject()(val controllerComponents: ControllerComponents, repository: LapsRepository)(implicit executionContext: ExecutionContext) extends BaseController {

  private def sortAndFilterLaps(laps: List[Laps]): List[Laps] = {
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
        List.empty[Laps]
    }
  }

  private def findAverageLaptime(laps: List[Laps]): JsArray = {
    val average = laps.flatMap(_.lap_duration).sum / laps.length
    Json.arr(Json.obj("average_laptime" -> toMinutesAndSeconds(average)))
  }

  def findByDriverNumberAndSession: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val params: Map[String, String] = Utilities.extractParams(request).toMap
    val route = "/laps"

    val driverNumber = params.get("driver_number")
    val sessionKey = params.get("session_key")

    (driverNumber, sessionKey) match {
      case (Some(""), _ ) => Future.successful(BadRequest(s"Error with request, missing driver number"))
      case (_ , Some("")) => Future.successful(BadRequest(s"Error with request, missing session key"))

      case (Some(_), Some(_)) =>
        repository.findByDriverAndSession(params).map {
          case Right(listOfLaps) if listOfLaps.nonEmpty =>
            val hotLaps = sortAndFilterLaps(listOfLaps.toList)
            val averageLap: JsArray = findAverageLaptime(hotLaps)
            if (hotLaps.isEmpty) {
              Ok(s"No completed laps for driver_number: ${driverNumber.get} and session_key:${sessionKey.get}")
            } else {
              implicit val ReadWriter: ReadWriter[LapData] = macroRW
              val laps = Laps.toLapData(hotLaps)
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
