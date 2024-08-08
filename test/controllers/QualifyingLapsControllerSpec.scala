package controllers

import base.TestData._
import base.ControllersSpecWithGuiceApp
import controllers.QualifyingLapsController
import models.QualifyingLaps
import org.mockito.ArgumentMatchers.{any, anyString}
import org.mockito.Mockito.when
import org.scalatest.matchers.must.Matchers._
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json._
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.Future

class QualifyingLapsControllerSpec extends ControllersSpecWithGuiceApp with MockitoSugar {
  val controller = new QualifyingLapsController(mockMcc, mockF1OpenApiController)

  "[QualifyingLapsController][findByDriverAndEvent]" should {

    val validQuery = "/quali?driver_last_name=Sainz&event_name=Imola"

    "return a 200 response and JSON response" in {
      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Right(sampleApiResponse)))

      val result = controller.findByDriverAndEvent().apply(FakeRequest(GET, validQuery))
      status(result) mustBe OK
    }

    "return a 200 response and JSON response for an empty result" in {
      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Right(List.empty)))

      val result = controller.findByDriverAndEvent().apply(FakeRequest(GET, validQuery))
      status(result) mustBe OK
    }

    "return a 400 response (BadRequest) when API call fails" in {
      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Left("Error with request")))

      val result = controller.findByDriverAndEvent().apply(FakeRequest(GET, validQuery))

      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("Error with request")
    }

    "return a 400 response (BadRequest) when driver_last_name is missing" in {
      val query = "/quali?event_name=Imola"

      val result = controller.findByDriverAndEvent().apply(FakeRequest(GET, query))

      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("Error with param keys")
    }

    "return a 400 response (BadRequest) when event_name is missing" in {
      val query = "/quali?driver_last_name=Sainz"

      val result = controller.findByDriverAndEvent().apply(FakeRequest(GET, query))

      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("Error with param keys")
    }

    "return a 400 response (BadRequest) when driver_last_name is invalid" in {
      val query = "/quali?driver_last_name=InvalidDriver&event_name=Imola"

      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Right(List.empty)))

      val result = controller.findByDriverAndEvent().apply(FakeRequest(GET, query))

      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("Error with param values")
    }

    "return a 400 response (BadRequest) when event_name is invalid" in {
      val query = "/quali?driver_last_name=Sainz&event_name=InvalidEvent"

      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Right(List.empty)))

      val result = controller.findByDriverAndEvent().apply(FakeRequest(GET, query))

      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("Error with param values")
    }
  }
}
