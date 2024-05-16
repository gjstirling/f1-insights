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
  private def apiRequest[T: Reads](url: String, params: Iterable[(String, String)]): Future[Either[String, T]] = {
    val response = requests.get(url, params = params)
    val json = Json.parse(response.data.array)

    json.validate[T] match {
      case JsSuccess(data, _) => Future.successful(Right(data))
      case JsError(errors) => Future.successful(Left(errors.mkString(", ")))
    }
  }
  private def extractParams(request: Request[AnyContent]): Iterable[(String, String)] = {
    request.queryString.flatMap {
      case (key, values) =>
        values.headOption.map(value => (key, value))
    }
  }


  def raceEvents: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val route = "/sessions"
    val params = extractParams(request)
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

  def getDriverQualifyingData: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val params: Iterable[(String, String)] = extractParams(request)

    val locationParam: Option[String] = params.find(_._1 == "location").map(_._2)
    val session_type = "Qualifying"

    locationParam match {
      case Some(location) =>
        // get session key
        val applyQualifyingFilter = params ++ Seq(("session_type", "Qualifying"))
        val search = repository.find(applyQualifyingFilter.toMap)

        Future.successful(Ok(search.toString()))
      case None =>
        Future.successful(BadRequest("No parameter for location found"))
    }
  }

  def insert(event: Seq[Event]): Unit = {
    repository.addAllEvents(event)
  }

  def find(filters: Map[String, String]): Seq[Event] = {
    repository.find(filters)
  }
}
