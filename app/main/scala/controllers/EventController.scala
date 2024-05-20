package main.scala.controllers

import main.scala.config.MyAppConfig
import main.scala.models.Event
import main.scala.repositories.EventRepository
import play.api.libs.json._
import play.api.mvc._
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
                                val f1Api: F1OpenApiController,
                                config: MyAppConfig) extends BaseController {
  private def extractParams(request: Request[AnyContent]): Iterable[(String, String)] = {
    request.queryString.flatMap {
      case (key, values) =>
        values.headOption.map(value => (key, value))
    }
  }

  def findAll: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val route = "/sessions"
    val params = extractParams(request)
    // limiting data to 2024 season
    val paramsWithYearFilter: Iterable[(String, String)] = params ++ Seq(("year", "2024"))

    f1Api.lookup[List[Event]](route, paramsWithYearFilter).map {
      case Right(race) =>
        implicit val eventRw: ReadWriter[Event] = macroRW

        // Filter out by session Key
        repository.insertEvents(race)

        val stringList = race.map { event => write(event) }
        val jsonList: List[JsValue] = stringList.map(Json.parse)
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

    locationParam match {
      case Some(location) =>
        // get session key
        val applyQualifyingFilter = params ++ Seq(("session_type", "Qualifying"))
        val search = repository.find(applyQualifyingFilter.toMap)

        Future.successful(Ok(search.headOption.toString))
      case None =>
        Future.successful(BadRequest("No parameter for location found"))
    }
  }
}
