package controllers

import base.TestData._
import base.ControllersSpecWithGuiceApp
import models.{LapData, QualifyingLaps}
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

  "[QualifyingLapsController][findByDriverNumberAndSession]" should {

    val validQuery = "/quali?driver_number=55&session_key=9999"

    "return a 200 response and JSON response" in {
      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Right(sampleApiResponse)))

      val result = controller.findByDriverNumberAndSession().apply(FakeRequest(GET, validQuery))
      status(result) mustBe OK
    }

    "return a 404 not found response for missing params" in {
      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Right(List.empty)))

      val result = controller.findByDriverNumberAndSession().apply(FakeRequest(GET, validQuery))
      status(result) mustBe NOT_FOUND
      contentAsString(result) must include("No qualifying laps found for the provided driver number and/or session key")
    }

    "return a 400 response (BadRequest) when API call fails" in {
      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Left("I Failed you")))

      val result = controller.findByDriverNumberAndSession().apply(FakeRequest(GET, validQuery))

      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("Error with request")
    }

    "return a 500 when parameters give no result " in {
      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Right(List.empty)))

      val result = controller.findByDriverNumberAndSession().apply(FakeRequest(GET, ""))
      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("Missing required parameters: 'driver_number' and/or 'session_key")
    }

    "return filtered laps sorted into correct order" in {
      val lap1 = QualifyingLaps("DATE", 55, Some(1), Some(1), Some(1), is_pit_out_lap = false, Some(10.2), 1, 1001, 9999, 320)
      val lap2 = QualifyingLaps("DATE", 55, Some(1), Some(1), Some(1), is_pit_out_lap = false, Some(11), 2, 1001, 9999, 315)
      val lap3 = QualifyingLaps("DATE", 55, Some(1), Some(1), Some(1), is_pit_out_lap = false, Some(10), 3, 1001, 9999, 325)
      val lapsList = List(lap1, lap2, lap3)

      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Right(lapsList)))

      val result = controller.findByDriverNumberAndSession().apply(FakeRequest(GET, "/quali?driver_number=55&session_key=9999"))
      status(result) mustBe OK

      val jsonResponse = contentAsJson(result).as[List[LapData]]

      jsonResponse.map(_.lap_number) mustBe List(1, 3)
      jsonResponse.head.lap_number mustBe 1
      jsonResponse(1).lap_number mustBe 3
    }

    "return an empty list laps have None for lap_duration" in {
      val lap1 = QualifyingLaps("2024-08-23T10:00:00Z", 55, None, Some(25.0), Some(27.345), is_pit_out_lap = false, None, 1, 1001, 9999, 320)
      val lapsList = List(lap1)

      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Right(lapsList)))

      val result = controller.findByDriverNumberAndSession().apply(FakeRequest(GET, "/quali?driver_number=55&session_key=9999"))
      status(result) mustBe OK

      val jsonResponse = contentAsJson(result).as[List[LapData]]
      jsonResponse mustBe empty
    }

    "return a 404 not found response when no laps are found" in {
      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Right(List.empty)))

      val result = controller.findByDriverNumberAndSession().apply(FakeRequest(GET, "/quali?driver_number=55&session_key=9999"))
      status(result) mustBe NOT_FOUND
      contentAsString(result) must include("No qualifying laps found for the provided driver number and/or session key")
    }

  }

}
