package controllers

import base.TestData._
import base.ControllersSpecWithGuiceApp
import main.controllers.QualifyingLapsController
import main.models.QualifyingLaps
import org.mockito.ArgumentMatchers.{any, anyString}
import org.mockito.Mockito.when
import org.scalatest.matchers.must.Matchers._
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json._
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.Future

class QualifyingLapsControllerSpec extends ControllersSpecWithGuiceApp with MockitoSugar {
  // Mock instances for dependencies
  val controller = new QualifyingLapsController(mockMcc, mockF1OpenApiController)

  "[QualifyingLapsController][findByDriverAndEvent]" should {

    val validQuery = "/quali?driver_last_name=Sainz&event_name=Imola"

    "return a 200 response and JSON response" in {
      // Mocks
      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Right(sampleApiResponse)))

      // Act
      val result = controller.findByDriverAndEvent().apply(FakeRequest(GET, validQuery))

      // Assertions
      status(result) mustBe OK
    }

    "return a 200 response and JSON response for an empty result" in {
      // Mocks
      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Right(List.empty)))

      // Act
      val result = controller.findByDriverAndEvent().apply(FakeRequest(GET, validQuery))

      // Assertions
      status(result) mustBe OK
    }

    "return a 400 response (BadRequest) when API call fails" in {
      // Mocks
      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Left("Error with request")))

      // Act
      val result = controller.findByDriverAndEvent().apply(FakeRequest(GET, validQuery))

      // Assertions
      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("Error with request")
    }

    "return a 400 response (BadRequest) when driver_last_name is missing" in {
      // Missing driver_last_name parameter
      val query = "/quali?event_name=Imola"

      // Act
      val result = controller.findByDriverAndEvent().apply(FakeRequest(GET, query))

      // Assertions
      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("Error with param keys")
    }

    "return a 400 response (BadRequest) when event_name is missing" in {
      // Missing event_name parameter
      val query = "/quali?driver_last_name=Sainz"

      // Act
      val result = controller.findByDriverAndEvent().apply(FakeRequest(GET, query))

      // Assertions
      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("Error with param keys")
    }

    "return a 400 response (BadRequest) when driver_last_name is invalid" in {
      // Invalid driver_last_name parameter
      val query = "/quali?driver_last_name=InvalidDriver&event_name=Imola"

      // Mocks
      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Right(List.empty)))

      // Act
      val result = controller.findByDriverAndEvent().apply(FakeRequest(GET, query))

      // Assertions
      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("Error with param values")
    }

    "return a 400 response (BadRequest) when event_name is invalid" in {
      // Invalid event_name parameter
      val query = "/quali?driver_last_name=Sainz&event_name=InvalidEvent"

      // Mocks
      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Right(List.empty)))

      // Act
      val result = controller.findByDriverAndEvent().apply(FakeRequest(GET, query))

      // Assertions
      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("Error with param values")
    }
  }
}
