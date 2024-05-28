package controllers

import base.ControllersSpecWithGuiceApp
import base.TestData._
import main.scala.controllers.EventController
import main.scala.models.Event
import main.scala.repositories.{EventRepository, F1OpenApi}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.matchers.must.Matchers._
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json._
import play.api.test._
import play.api.test.Helpers._
import scala.concurrent.Future

class EventControllerSpec extends ControllersSpecWithGuiceApp with MockitoSugar {
  // controller instance for tests and mocks
  val controller = new EventController(
    controllerComponents = mockMcc,
    repository = mockEventRepository,
    f1Api = mockF1OpenApiController
  )(ec)

  "\n[EventController][findAll]" should {

    "return a 200 response and json response" in {
      // Mocks
      when(mockF1OpenApiController.lookup[List[Event]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[Event]]]))
        .thenReturn(Future.successful(Right(sampleEvents)))
      doNothing().when(mockEventRepository).insertEvents(sampleEvents)

      // act
      val result = controller.findAll().apply(FakeRequest(GET, "/"))

      //assertions
      status(result) mustBe OK
      contentAsJson(result) mustBe jsonResponse
    }

    "return a 400 response (BadRequest) when API call fails" in {
      // Mocks
      val errorMessage = "[EventController][findAll]:      Error with request"
      when(mockF1OpenApiController.lookup[List[Event]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[Event]]]))
        .thenReturn(Future.successful(Left("error from API")))

      // act
      val result = controller.findAll().apply(FakeRequest(GET, "/"))

      // assertions
      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include(errorMessage)
    }

  }
}
