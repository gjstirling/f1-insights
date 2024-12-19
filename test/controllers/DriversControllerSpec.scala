package controllers

import base.ControllersSpecWithGuiceApp
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.must.Matchers._
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.{DriversRepository, EventsRepository}

import scala.concurrent.Future


class DriversControllerSpec extends ControllersSpecWithGuiceApp with MockitoSugar {

  val mockDriversRepository: DriversRepository = mock[DriversRepository]

  val controller = new DriversController(mockMcc, mockDriversRepository)

  "[DriversController][findAll]" should {

    "return a 200 response and JSON response of driver name's and numbers" in {
      when(mockDriversRepository.findAll(any[Map[String, String]])).thenReturn(Future.successful(mockDriversList))
      val result = controller.index().apply(FakeRequest(GET, ""))

      status(result) mustBe 200
      contentAsString(result) must include(s"[{\"driver_number\":44,\"full_name\":\"Lewis Hamilton\"},{\"driver_number\":33,\"full_name\":\"Max Verstappen\"}]")
    }

    "Handle server error when database call fails" in {
      when(mockDriversRepository.findAll(any[Map[String, String]])).thenReturn(Future.failed(new Exception))
      val result = controller.index().apply(FakeRequest(GET, ""))

      status(result) mustBe 500
      contentAsString(result) must include("An error occurred while fetching driver details:")
    }
  }
}

