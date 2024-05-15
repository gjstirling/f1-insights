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
import scala.concurrent.{ExecutionContext}
import javax.inject._

@Singleton
class EventController @Inject()(implicit executionContext: ExecutionContext,
                                val controllerComponents: ControllerComponents,
                                val repository: EventRepository,
                                config: MyAppConfig) extends BaseController {

  def index(): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] => {
      val route = "/sessions"
      val queryParams: Map[String, Seq[String]] = request.queryString
      val params: Iterable[(String, String)] = queryParams.flatMap {
        case (key, values) =>
          values.headOption.map(value => (key, value))
      }
      val response: Response = requests.get(config.apiBaseUrl + route, params = params)
      val json = Json.parse(response.data.array).validate[List[Event]]

      json match {
        case JsSuccess(race, _) =>
          // Store request body
          dbTestAll(race)

          val eventJsonList: List[String] = race.map {
            event =>
              implicit val eventRw: ReadWriter[Event] = macroRW
              write(event)
          }
          val jsonList: List[JsValue] = eventJsonList.map(Json.parse)
          val jsonArray: JsArray = JsArray(jsonList)
          Ok(jsonArray)

        case JsError(errors) =>
          MyLogger.red(s"Failed to parse: $errors")
          BadRequest("Error with request")
      }
    }
  }

  def dbTest(event: Event): Unit = {
    repository.addOneEvent(event)
  }

  def dbTestAll(event: List[Event]): Unit = {
    repository.addAllEvents(event)
  }

}
