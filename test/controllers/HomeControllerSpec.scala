package controllers

import base.ControllersSpecWithGuiceApp
import base.TestData._
import main.scala.controllers.HomeController
import main.scala.models.Event
import main.scala.repositories.{EventRepository, F1OpenApi}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.matchers.must.Matchers._
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json._
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.Future

class HomeControllerSpec extends ControllersSpecWithGuiceApp with MockitoSugar {

  val controller = new HomeController(
    controllerComponents = mockMcc,
  )(ec)

  "\n[EventController][findAll]" should {

    "return a 200 response and json response" in {
      val result = controller.index().apply(FakeRequest(GET, "/"))

      // assertions
      status(result) mustBe OK
      contentAsString(result) must include(controller.apiReponse)

    }

  }
}
