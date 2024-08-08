package controllers

import models.Event
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
    val eventsFuture: Future[Seq[Event]] = repository.findAll()

    eventsFuture.map { events =>
      val eventsJson: JsValue = Json.toJson(events)

      Ok(eventsJson)
    }.recover {
      case ex: Exception =>
        InternalServerError("An error occurred while fetching events: " + ex.getMessage)
    }
  }
}