package main.scala.controllers

import main.scala.config.MyAppConfig
import main.scala.models.Event
import main.scala.repositories.EventRepository
import play.api.libs.json._
import play.api.mvc._
import requests.Response
import services.MyLogger
import upickle.default._
import play.api.libs.json.{Json, __}

import scala.concurrent.ExecutionContext
import javax.inject._

import scala.concurrent.Future

@Singleton
class EventController @Inject()(implicit executionContext: ExecutionContext,
                                val controllerComponents: ControllerComponents,
                                val repository: EventRepository,
                                config: MyAppConfig) extends BaseController {

  def raceEvents(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val route = "/sessions"
    val params: Iterable[(String, String)] = request.queryString.flatMap {
      case (key, values) =>
        values.headOption.map(value => (key, value))
    }
    // limiting data to 2024 season
    val paramsWithYearFilter: Iterable[(String, String)] = params ++ Seq(("year", "2024"))

    apiRequest[List[Event]](config.apiBaseUrl + route, paramsWithYearFilter).map {
      case Right(race) =>
        val eventJsonList: List[String] = race.map { event =>
          implicit val eventRw: ReadWriter[Event] = macroRW
          write(event)
        }
        // Add data to events collection ?
        insert(race)
        val jsonList: List[JsValue] = eventJsonList.map(Json.parse)
        val jsonArray: JsArray = JsArray(jsonList)
        Ok(jsonArray)

      case Left(errors) =>
        MyLogger.red(s"Failed to parse: $errors")
        BadRequest("Error with request")
    }
  }

  private def apiRequest[T: Reads](url: String, params: Iterable[(String, String)]): Future[Either[String, T]] = {
    val response = requests.get(url, params = params)
    val json = Json.parse(response.data.array)

    json.validate[T] match {
      case JsSuccess(data, _) => Future.successful(Right(data))
      case JsError(errors) => Future.successful(Left(errors.mkString(", ")))
    }
  }
  def insert(event: List[Event]): Unit = {
    repository.addAllEvents(event)
  }

  def find(location: String): Seq[Event] = {
    repository.find(location)
  }

}


// Use later ?
//  def insert(event: Event): Unit = {
//    repository.addOneEvent(event)
//  }
