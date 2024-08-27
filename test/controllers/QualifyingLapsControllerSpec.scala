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

    "Ok 200:" when {
      "return a JSON response with Lap data" in {
        when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
          anyString(),
          any[Iterable[(String, String)]]
        )(any[Reads[List[QualifyingLaps]]]))
          .thenReturn(Future.successful(Right(sampleApiResponse)))

        val result = controller.findByDriverNumberAndSession().apply(FakeRequest(GET, validQuery))
        val expectedJson = Json.parse(
          """[{"lap_number":2,"sector_1":24.699,
            |"sector_2":26.612,"sector_3":25.664,"lap_time":"1m16.975"},
            |{"average_laptime":"1m16.975"}]""".stripMargin)
        val actualJson = Json.parse(contentAsString(result))

        status(result) mustBe OK
        actualJson mustEqual expectedJson
      }


      "return filtered laps sorted into correct order" in {
        val lap1 = QualifyingLaps("DATE", 55, Some(20), Some(30), Some(20), is_pit_out_lap = false, Some(70), 1, 1001, 9999, None)
        val lap2 = QualifyingLaps("DATE", 55, Some(20), Some(30), Some(19), is_pit_out_lap = false, Some(69), 2, 1001, 9999, None)
        val lap3 = QualifyingLaps("DATE", 55, Some(20), Some(30), Some(18), is_pit_out_lap = false, Some(68), 3, 1001, 9999, None)
        val lapsList = List(lap1, lap2, lap3)
        when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
          anyString(),
          any[Iterable[(String, String)]]
        )(any[Reads[List[QualifyingLaps]]]))
          .thenReturn(Future.successful(Right(lapsList)))

        val result = controller.findByDriverNumberAndSession().apply(FakeRequest(GET, validQuery))

        val expectedJson = Json.parse(
          """[{"lap_number":2,"sector_1":20,"sector_2":30,"sector_3":19,"lap_time":
            |"1m9.0"},{"lap_number":3,"sector_1":20,"sector_2":30,"sector_3":18,"lap_time":"1m8.0"},
            |{"average_laptime":"1m8.5"}]""".stripMargin)
        val actualJson = Json.parse(contentAsString(result))
        status(result) mustBe OK
        actualJson mustEqual expectedJson
      }

      "Return a response When there are no valid laps returned from API (No lap_duration)" in {
        val lap = QualifyingLaps("2024-08-23T10:00:00Z", 55, None,
          Some(25.0), Some(27.345), is_pit_out_lap = false, None, 1, 1001, 9999, None)
        val lapsList = List(lap)

        when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
          anyString(),
          any[Iterable[(String, String)]]
        )(any[Reads[List[QualifyingLaps]]]))
          .thenReturn(Future.successful(Right(lapsList)))

        val result = controller.findByDriverNumberAndSession().apply(FakeRequest(GET, validQuery))
        status(result) mustBe OK
        contentAsString(result) mustBe "No completed laps for driver_number: 55 and session_key:9999"
      }

    }


    "BadRequest 400:" when {

      "missing session key parameter" in {
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

      "missing driver number parameter" in {
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

      "session_key value is missing" in {
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

      "driver_number value is missing" in {
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

      "API call fails" in {
        when(mockF1OpenApiController.lookup[List[QualifyingLaps]](
          anyString(),
          any[Iterable[(String, String)]]
        )(any[Reads[List[QualifyingLaps]]]))
          .thenReturn(Future.successful(Left("I Failed you")))

        val result = controller.findByDriverNumberAndSession().apply(FakeRequest(GET, validQuery))

        status(result) mustBe BAD_REQUEST
        contentAsString(result) must include("Error with request")
      }


    }

    "NotFound 404: missing session key parameter" when {

      "API returns no results" in {
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

      "no laps are found given valid parameters" in {
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
}


