package controllers

import base.ControllersSpecWithGuiceApp
import base.TestData._
import main.scala.config.MyAppConfig
import main.scala.controllers.QualifyingLapsController
import main.scala.models.{Event, LapData, QualifyingLaps}
import main.scala.repositories.F1OpenApi
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
  val mockF1OpenApiController: F1OpenApi = mock[F1OpenApi]
  val mockMyAppConfig: MyAppConfig = mock[MyAppConfig]

  val controller = new QualifyingLapsController(mockMcc, mockF1OpenApiController, mockMyAppConfig)

  "[QualifyingLapsController][find]" should {

    "return a 200 response and JSON response" in {
      // Mocks
      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Right(sampleApiResponse)))

      // Act
      val result = controller.find().apply(FakeRequest(GET, "/quali"))

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
      val result = controller.find().apply(FakeRequest(GET, "/quali"))

      // assertions
      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("Error with request")
    }
  }
}
