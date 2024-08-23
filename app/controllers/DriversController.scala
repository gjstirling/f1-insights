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
    val params: Map[String, String] = Map.empty
    val driverSearchResult: Future[Seq[Drivers]] = repository.findAll(params)

    driverSearchResult.map { drivers =>
      val nameAndNumber = drivers.map { driver =>
        Drivers.getNameAndNumber(driver)
      }

      val json: JsValue = Json.toJson(nameAndNumber)
      Ok(json)
    }.recover {
      case ex: Exception =>
        InternalServerError("An error occurred while fetching driver details: " + ex.getMessage)
    }
  }

}