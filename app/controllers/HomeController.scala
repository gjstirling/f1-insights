package controllers

import controllers.HomeController.DateFormat
import upickle.default._
import play.api.libs.json.{JsError, JsSuccess, Json}

import javax.inject._
import play.api.mvc._
import requests.Response
import models.Event
import services.MyLogger

import java.time.{ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter

object HomeController {
  private val DateFormat = "MM/dd/yyyy"
}
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  private def formatDate(timestamp: String): String = {
    val zonedDateTime = ZonedDateTime.parse(timestamp)
    val instant = zonedDateTime.toInstant

    DateTimeFormatter
      .ofPattern(DateFormat)
      .withZone(ZoneId.systemDefault())
      .format(instant)
  }
  private def buildUri(session: String, year: Int): String = {
    val baseUrl = "https://api.openf1.org/v1"
    val route = "/sessions"
    val query = s"?session_name=${session}&year=${year}"

    s"$baseUrl$route$query"
  }
  private def formatEvent(event: Event) = {
    s"""
       |${event.location} GP ${formatDate(event.date_start)}
       |  ${event.session_name}
       |  Session key: ${event.session_key}
       |""".stripMargin
  }

  def index(session: String, year: Int): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val response: Response = requests.get(buildUri(session, year))
    val json = Json.parse(response.data.array).validate[List[Event]]

    json match {
      case JsSuccess(race, _) =>
        val eventJsonList: List[String] = race.map { event =>
          MyLogger.info(s"${formatEvent(event)}")
          implicit val eventRw: ReadWriter[Event] = macroRW
          val json: String = write(event)
          MyLogger.red(json)
          json
        }
        Ok(Json.arr(eventJsonList))

      case JsError(errors) =>
        MyLogger.red(s"Failed to parse: $errors")
        BadRequest("Error with request")
    }
  }
}
