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
  val validQuery = "/quali?driver_number=55&session_key=9999"

  "[QualifyingLapsController][findByDriverNumberAndSession]" should {

    "Okay: returns a JSON response with Lap data" in {
      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Right(sampleApiResponse)))

      val result = controller.findByDriverNumberAndSession().apply(FakeRequest(GET, validQuery))
      status(result) mustBe OK
      contentAsString(result) must include(s"[{\"lap_number\":2,\"sector_1\":24.699,\"sector_2\":26.612,\"sector_3\":25.664,\"lap_time\":\"1m16.975\"}]")
    }

    "Okay: returns filtered laps sorted into correct order" in {
      val lap1 = QualifyingLaps("DATE", 55, Some(1), Some(1), Some(1), is_pit_out_lap = false, Some(10.2), 1, 1001, 9999, None)
      val lap2 = QualifyingLaps("DATE", 55, Some(1), Some(1), Some(1), is_pit_out_lap = false, Some(11), 2, 1001, 9999, None)
      val lap3 = QualifyingLaps("DATE", 55, Some(1), Some(1), Some(1), is_pit_out_lap = false, Some(10), 3, 1001, 9999, None)
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
    }

    "Okay: returns an empty list of laps when there are no valid laps" in {
      val lap1 = QualifyingLaps("2024-08-23T10:00:00Z", 55, None, Some(25.0), Some(27.345), is_pit_out_lap = false, None, 1, 1001, 9999, None)
      val lapsList = List(lap1)

      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Right(lapsList)))

      val result = controller.findByDriverNumberAndSession().apply(FakeRequest(GET, validQuery))
      status(result) mustBe OK

      val jsonResponse = contentAsJson(result).as[List[LapData]]
      jsonResponse mustBe empty
    }

    "BadRequest: missing session key parameter" in {
      val invalidQuery = "/quali?driver_number=55"
      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Right(List.empty)))

      val result = controller.findByDriverNumberAndSession().apply(FakeRequest(GET, invalidQuery))
      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("Missing required parameters: 'driver_number' and/or 'session_key'")
    }

    "BadRequest: missing driver number parameter" in {
      val invalidQuery = "/quali?session_key=9999"
      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Right(List.empty)))

      val result = controller.findByDriverNumberAndSession().apply(FakeRequest(GET, invalidQuery))
      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("Missing required parameters: 'driver_number' and/or 'session_key'")
    }

    "BadRequest: session_key value is missing" in {
      val invalidQuery = "/quali?driver_number=55&session_key="
      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Right(List.empty)))

      val result = controller.findByDriverNumberAndSession().apply(FakeRequest(GET, invalidQuery))
      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("Error with request, missing session key")
    }

    "BadRequest: driver_number value is missing" in {
      val invalidQuery = "/quali?driver_number=&session_key=9999"
      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Right(List.empty)))

      val result = controller.findByDriverNumberAndSession().apply(FakeRequest(GET, invalidQuery))
      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("Error with request, missing driver number")
    }

    "BadRequest: API call fails" in {
      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Left("I Failed you")))

      val result = controller.findByDriverNumberAndSession().apply(FakeRequest(GET, validQuery))

      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("Error with request")
    }

    "NotFound: API returns no results" in {
      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Right(List.empty)))

      val result = controller.findByDriverNumberAndSession().apply(FakeRequest(GET, validQuery))
      status(result) mustBe NOT_FOUND
      contentAsString(result) must include("No qualifying laps found for the provided driver number " +
        "and/or session key. Please check and try again")
    }

    "Not Found: no laps are found given valid parameters" in {
      when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
        anyString(),
        any[Iterable[(String, String)]]
      )(any[Reads[List[QualifyingLaps]]]))
        .thenReturn(Future.successful(Right(List.empty)))

      val result = controller.findByDriverNumberAndSession().apply(FakeRequest(GET, validQuery))
      status(result) mustBe NOT_FOUND
      contentAsString(result) must include("No qualifying laps found for the provided driver number and/or session key")
    }

  }

}
