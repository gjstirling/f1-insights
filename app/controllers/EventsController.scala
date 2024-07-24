package controllers

import models.Event
import repositories.EventsRepository
import connectors.F1OpenApi
import play.api.libs.json._
import play.api.mvc._
import services.Services.convertToJsonArray
import upickle.default._
import services.{MyLogger, Services}

import scala.concurrent.ExecutionContext
import javax.inject._

@Singleton
class EventsController @Inject()(
                                 val controllerComponents: ControllerComponents,
                                 val repository: EventsRepository,
                                 val f1Api: F1OpenApi
                               )(implicit val executionContext: ExecutionContext) extends BaseController {
  def index: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val route = "/sessions"
    val params = Services.extractParams(request)
    // limiting data to 2024 season and Qualifying only
    val paramsWithFilters: Iterable[(String, String)] = params ++ Seq(("year", "2024"), ("session_name", "Qualifying"))

    f1Api.lookup[List[Event]](route, paramsWithFilters).map {
      case Right(race) =>
        implicit val eventRw: ReadWriter[Event] = macroRW
        repository.insert(race)

        val jsonArray: JsArray = convertToJsonArray(race)
        Ok(jsonArray)

      case Left(errors) =>
        BadRequest("[EventController][findAll]:      Error with request")
    }
  }
}