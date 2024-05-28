package controllers

import base.ControllersSpecWithGuiceApp
import base.TestData._
import main.scala.controllers.QualifyingLapsController
import main.scala.models.QualifyingLaps
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.matchers.must.Matchers._
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json._
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.Future

class QualifyingLapsControllerSpec extends ControllersSpecWithGuiceApp with MockitoSugar {
  // Mock instances for dependencies
  val controller = new QualifyingLapsController(mockMcc, mockF1OpenApiController, mockMyAppConfig)

  "[QualifyingLapsController][find]" should {

    val query = "/quali?driver_last_name=Sainz&event_name=Imola"

    "return a 200 response and JSON response" in {
      // Mocks
      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Right(sampleApiResponse)))

      // Act
      val result = controller.findByDriverAndEvent().apply(FakeRequest(GET, query))

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

      // act
      val result = controller.findByDriverAndEvent().apply(FakeRequest(GET, query))

      // assertions
      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("Error with request")
    }
  }
}
