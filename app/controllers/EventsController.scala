package controllers

import models.{Event, ShortEvent}
import repositories.EventsRepository
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import javax.inject._

@Singleton
class EventsController @Inject()(
                                 val controllerComponents: ControllerComponents,
                                 val repository: EventsRepository
                               )(implicit val executionContext: ExecutionContext) extends BaseController {
  def index: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val params = Map("session_name" -> "Qualifying")
    val eventsFuture: Future[Seq[Event]] = repository.findAll(params)

    eventsFuture.map { events =>
      val shortEvents = events.map { event =>
        Event.convertToShort(event)
      }

      val eventsJson: JsValue = Json.toJson(shortEvents)
      Ok(eventsJson)
    }.recover {
      case ex: Exception =>
        InternalServerError("An error occurred while fetching events: " + ex.getMessage)
    }
  }

}