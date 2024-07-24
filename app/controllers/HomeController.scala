package controllers

import play.api.mvc._
import javax.inject._
import scala.concurrent.ExecutionContext

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               )(implicit val executionContext: ExecutionContext) extends BaseController {

  val apiReponse = "Welcome to the F1 Insights API !!!"

  def index: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(apiReponse)
  }

}
