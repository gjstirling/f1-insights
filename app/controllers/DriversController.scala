package controllers

import models.Drivers
import play.api.libs.json._
import play.api.mvc._
import repositories.DriversRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DriversController @Inject()(
                                 val controllerComponents: ControllerComponents,
                                 val repository: DriversRepository
                               )(implicit val executionContext: ExecutionContext) extends BaseController {

  def index: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val driverSearchResult: Future[Seq[Drivers]] = repository.findAll(Map.empty)

    driverSearchResult.map { drivers =>
      val json: JsValue = Json.toJson(drivers)
      Ok(json)
    }.recover {
      case ex: Exception =>
        InternalServerError("An error occurred while fetching driver details: " + ex.getMessage)
    }
  }

}