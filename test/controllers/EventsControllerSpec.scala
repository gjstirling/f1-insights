//package controllers
//
//import base.ControllersSpecWithGuiceApp
//import org.mockito.ArgumentMatchers.any
//import org.mockito.Mockito.when
//import org.scalatestplus.mockito.MockitoSugar
//import play.api.test.FakeRequest
//import play.api.test.Helpers.{GET, status}
//import org.scalatest.matchers.must.Matchers._
//import play.api.test.Helpers._
//import repositories.EventsRepository
//
//import scala.concurrent.Future
//
//
//class EventsControllerSpec extends ControllersSpecWithGuiceApp with MockitoSugar {
//
//  val mockEventsRepository: EventsRepository = mock[EventsRepository]
//
//  val controller = new EventsController(mockMcc, mockEventsRepository)
//
//  "[EventsController][findAll]" should {
//
//    "return a 200 response and JSON response of events" in {
//      when(mockEventsRepository.findAll(any[Map[String, String]])).thenReturn(Future.successful(mockEventList))
//      val result = controller.index().apply(FakeRequest(GET, ""))
//
//      status(result) mustBe 200
//      contentAsString(result) must include(s"[{\"session_key\":1,\"session_name\":\"Practice 1\",\"location\":\"Silverstone\"}]")
//    }
//
//    "return a server error when repository call fails" in {
//      when(mockEventsRepository.findAll(any[Map[String, String]])).thenReturn(Future.failed(new Exception))
//      val result = controller.index().apply(FakeRequest(GET, ""))
//
//      status(result) mustBe 500
//      contentAsString(result) must include("An error occurred while fetching events:")
//    }
//  }
//}
//
