package main.scala.controllers

import main.scala.config.MyAppConfig
import main.scala.repositories.EventRepository
import play.api.mvc._
import services.Services.{MyLogger, convertToJsonArray}
import upickle.default._
import main.scala.models.{LapData, QualifyingLaps}
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites

import scala.concurrent.ExecutionContext
import javax.inject._
import services.Services

@Singleton
class QualifyingLapsController @Inject()(implicit executionContext: ExecutionContext,
                                         val controllerComponents: ControllerComponents,
                                         val repository: EventRepository,
                                         val f1Api: F1OpenApiController,
                                         config: MyAppConfig) extends BaseController {

  def find: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val params = Services.extractParams(request)
    val route = "/laps"

    f1Api.lookup[List[QualifyingLaps]](route, params).map {
      case Right(listOfLaps) =>
        implicit val eventRw: ReadWriter[LapData] = macroRW
        val convertToLaps = listOfLaps.map { lap => LapData(lap_number = lap.lap_number, lap.duration_sector_1, lap.duration_sector_2, lap.duration_sector_3, lap.lap_duration )}

        // Convert the list of maps to a JSON array
        val jsonArray = convertToJsonArray(convertToLaps)
        Ok(jsonArray)

      case Left(errors) =>
        MyLogger.red(s"Failed to parse: $errors")
        BadRequest("[QualifyingLapsController][find]:     Error with request")
    }
  }
}
