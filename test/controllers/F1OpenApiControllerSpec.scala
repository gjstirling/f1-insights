package controllers

import main.scala.controllers.F1OpenApiController
import services.ApiClient
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import play.api.libs.json.{Json, Reads}
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.{ExecutionContext, Future}

class F1OpenApiControllerSpec extends AsyncFlatSpec with Matchers {
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  class MockApiClient extends ApiClient {
    override def get(route: String, params: Iterable[(String, String)]): Future[Either[String, Array[Byte]]] = {
      // Return a mock response here
      Future.successful(Right("""{"mock_key": "mock_value"}""".getBytes))
    }
  }

  case class MockResponse(mock_key: String)
  implicit val mockResponseReads: Reads[MockResponse] = Json.reads[MockResponse]
  val mockApiClient = new MockApiClient()
  val controllerComponents: ControllerComponents = stubControllerComponents()
  val controller = new F1OpenApiController(controllerComponents, mockApiClient)

  "\n[F1OpenApiController][lookup]" should "return mock data on success" in {
    controller.lookup[MockResponse]("/mock_route", Seq.empty).map {
      case Right(response) =>
        response.mock_key shouldBe "mock_value"
      case Left(error) =>
        fail(s"Expected successful response but got error: $error")
    }
  }

  // Simulate an error response
  it should "handle API failure gracefully" in {
    val mockApiClientWithError = new ApiClient {
      override def get(route: String, params: Iterable[(String, String)]): Future[Either[String, Array[Byte]]] = {
        // API Call fails handling
        Future.successful(Left("API call failed"))
      }
    }
    val failingController = new F1OpenApiController(controllerComponents, mockApiClientWithError)

    // act and asser
    failingController.lookup[MockResponse]("/mock_route", Seq.empty).map {
      case Right(_) =>
        fail("Expected API call to fail but it succeeded")
      case Left(error) =>
        error shouldBe "API call failed"
    }
  }
}

