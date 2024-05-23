package main.scala.controllers

import main.scala.config.MyAppConfig
import main.scala.repositories.{EventRepository, F1OpenApi}
import play.api.mvc._
import services.Services.convertToJsonArray
import services.MyLogger
import upickle.default._
import main.scala.models.{LapData, QualifyingLaps}

import scala.concurrent.ExecutionContext
import javax.inject._
import services.Services


@Singleton
class QualifyingLapsController @Inject()(
                                         val controllerComponents: ControllerComponents,
                                         val f1Api: F1OpenApi,
                                         config: MyAppConfig)(implicit executionContext: ExecutionContext) extends BaseController {

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

  def find: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val params: Map[String, String] = Services.extractParams(request).toMap
    val route = "/laps"

    // check driver number and session key are valid from maps ???
    // return a custom response

      f1Api.lookup[List[QualifyingLaps]](route, params).map {
        case Right(listOfLaps) =>
          val hotLaps = sortAndFilterLaps(listOfLaps)
          implicit val eventRw: ReadWriter[LapData] = macroRW
          val convertToLaps = hotLaps.map { lap => LapData(lap_number = lap.lap_number, lap.duration_sector_1, lap.duration_sector_2, lap.duration_sector_3, lap.lap_duration) }

          // Convert the list of maps to a JSON array
          val jsonArray = convertToJsonArray(convertToLaps)
          Ok(jsonArray)

        case Left(errors) =>
          //MyLogger.red(s"[QualifyingLapsController][find]:     Error with request: $errors")
          BadRequest("Error with request")
      }
  }
}
