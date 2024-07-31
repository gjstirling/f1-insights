package controllers

import connectors.F1OpenApi
import models.{LapData, QualifyingLaps}
import play.api.mvc._
import services.Services.convertToJsonArray
import upickle.default._

import scala.concurrent.{ExecutionContext, Future}
import javax.inject._
import services.{MyLocalRepo, Services}


@Singleton
class QualifyingLapsController @Inject()(val controllerComponents: ControllerComponents,
                                         val f1Api: F1OpenApi)(implicit executionContext: ExecutionContext) extends BaseController {

  private def sortAndFilterLaps(laps: List[QualifyingLaps]): List[QualifyingLaps] = {
    val sortedLaps = laps.sortBy(_.lap_duration.getOrElse(Double.MaxValue))
    val fastestLapOpt = sortedLaps.headOption.flatMap(_.lap_duration)

    fastestLapOpt match {
      case Some(fastestLap) =>
        val filteredLaps = sortedLaps.filter { lap =>
          lap.lap_duration.exists(_ <= fastestLap * 1.02)
        }
        filteredLaps.sortBy(_.lap_number)

      case None =>
        List.empty[QualifyingLaps]
    }
  }

  private def QualifyingLapsToLapData(qualiLaps: List[QualifyingLaps]): List[LapData] = {
    qualiLaps.map { lap =>
      val minutes = if (lap.lap_duration.get > 60.00) 1
      val seconds = ((lap.lap_duration.get - 60) * 1000).round / 1000.toDouble
      val totalLapTime = s"${minutes}m$seconds"

      LapData(
        lap_number = lap.lap_number,
        lap.duration_sector_1.getOrElse(0),
        lap.duration_sector_2.getOrElse(0),
        lap.duration_sector_3.getOrElse(0),
        totalLapTime
      )
    }

  }

  def findByDriverAndEvent: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val params: Map[String, String] = Services.extractParams(request).toMap
    val route = "/laps"

    val driverNameOpt = params.get("driver_last_name")
    val eventNameOpt = params.get("event_name")

    (driverNameOpt, eventNameOpt) match {
      case (Some(driverName), Some(eventName)) =>
        val driverNumberOpt = MyLocalRepo.lastNameToRaceNumberMap.get(driverName)
        val sessionKeyOpt = MyLocalRepo.circuitSessionKeyMap.get(eventName)

        (driverNumberOpt, sessionKeyOpt) match {
          case (Some(driverNumber), Some(sessionKey)) =>
            val apiParams = Map("driver_number" -> driverNumber, "session_key" -> sessionKey)

            f1Api.lookup[List[QualifyingLaps]](route, apiParams).map {
              case Right(listOfLaps) =>
                val hotLaps = sortAndFilterLaps(listOfLaps)
                implicit val eventRw: ReadWriter[LapData] = macroRW
                val laps = QualifyingLapsToLapData(hotLaps)
                val jsonArray = convertToJsonArray(laps)
                Ok(jsonArray)

              case Left(errors) =>
                BadRequest(s"Error with request:  $errors")
            }
          case _ =>
            Future.successful(BadRequest("Error with param values"))
        }

      case _ =>
        Future.successful(BadRequest("Error with param keys"))
    }
  }

}
