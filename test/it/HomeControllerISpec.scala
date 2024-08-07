package it

import base.ControllersSpecBase
import controllers.HomeController
import play.api.test._
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status, stubControllerComponents}

class HomeControllerISpec extends ControllersSpecBase {
  import scala.concurrent.ExecutionContext.Implicits.global

  "HomeController" should {
    "return the welcome message" in {
        WsTestClient.withClient { client =>
          val homeController = new HomeController(controllerComponents = stubControllerComponents())(global)
          val result = homeController.index().apply(FakeRequest(method = "GET", "/"))

          status(result) mustBe OK
          val bodyText = contentAsString(result)
          bodyText shouldEqual "Welcome to the F1 Insights API !!!"
        }
      }
    }
}
