package controllers

import base.ControllersSpecWithGuiceApp
import base.TestData._
import main.scala.controllers.{EventController, F1OpenApiController}
import main.scala.models.Event
import main.scala.repositories.EventRepository
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
  val mockEventRepository: EventRepository = mock[EventRepository]
  val mockF1OpenApiController: F1OpenApiController = mock[F1OpenApiController]

  val controller = new EventController(
    controllerComponents = mockMcc,
    repository = mockEventRepository,
    f1Api = mockF1OpenApiController
  )(ec)

  "EventController GET" should {

    "This test should pass (assert sbt test works)" in {
      assert(Set.empty.size === 0)
    }

    "findAll: Returns a list of race events" in {
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
  }
}
