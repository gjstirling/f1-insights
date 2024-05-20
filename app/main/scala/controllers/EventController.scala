package main.scala.controllers

import main.scala.config.MyAppConfig
import main.scala.models.Event
import main.scala.repositories.EventRepository
import play.api.libs.json._
import play.api.mvc._
import services.Services.{MyLogger, convertToJsonArray}
import upickle.default._
import play.api.libs.json.{Json, __}
import services.Services

import scala.concurrent.ExecutionContext
import javax.inject._
import scala.concurrent.Future

@Singleton
class EventController @Inject()(implicit executionContext: ExecutionContext,
                                val controllerComponents: ControllerComponents,
                                val repository: EventRepository,
                                val f1Api: F1OpenApiController,
                                config: MyAppConfig) extends BaseController {
  def findAll: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val route = "/sessions"
    val params = Services.extractParams(request)
    // limiting data to 2024 season and Qualifying only
    val paramsWithFilters: Iterable[(String, String)] = params ++ Seq(("year", "2024"), ("session_name", "Qualifying"))

    f1Api.lookup[List[Event]](route, paramsWithFilters).map {
      case Right(race) =>
        implicit val eventRw: ReadWriter[Event] = macroRW

        // Filter out by session Key
        repository.insertEvents(race)

        val jsonArray: JsArray = convertToJsonArray(race)
        Ok(jsonArray)

      case Left(errors) =>
        MyLogger.red(s"Failed to parse: $errors")
        BadRequest("[EventController][findAll]:      Error with request")
    }
  }
}
