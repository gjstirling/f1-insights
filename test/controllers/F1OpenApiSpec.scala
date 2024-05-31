package controllers

import main.scala.connectors.{ApiClient, F1OpenApi}
import play.api.libs.json.{Json, Reads}
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.{ExecutionContext, Future}

class F1OpenApiSpec extends AsyncFlatSpec with Matchers {
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
  val controller = new F1OpenApi(mockApiClient)

  "\n[F1OpenApi][lookup]" should "return mock data on success" in {
    controller.lookup[MockResponse]("/mock_route", Seq.empty).map {
      case Right(response) =>
        response.mock_key shouldBe "mock_value"
      case Left(error) =>
        fail(s"Expected successful response but got error: $error")
    }
  }

  it should "handle API failure gracefully" in {
    val mockApiClientWithError = new ApiClient {
      override def get(route: String, params: Iterable[(String, String)]): Future[Either[String, Array[Byte]]] = {
        // API Call fails handling
        Future.successful(Left("API call failed"))
      }
    }
    val failingController = new F1OpenApi(mockApiClientWithError)

    failingController.lookup[MockResponse]("/mock_route", Seq.empty).map {
      case Right(_) =>
        fail("Expected API call to fail but it succeeded")
      case Left(error) =>
        error shouldBe "API call failed"
    }
  }

  it should "handle JsError when JSON response cannot be parsed" in {
    val mockApiClientWithInvalidJson = new ApiClient {
      override def get(route: String, params: Iterable[(String, String)]): Future[Either[String, Array[Byte]]] = {
        // Return an invalid JSON
        Future.successful(Right("""{"invalid_key": "invalid_value"}""".getBytes))
      }
    }
    val controllerWithInvalidJson = new F1OpenApi(mockApiClientWithInvalidJson)

    controllerWithInvalidJson.lookup[MockResponse]("/mock_route", Seq.empty).map {
      case Right(_) =>
        fail("Expected JSON parsing to fail but it succeeded")
      case Left(error) =>
        error should include("error.path.missing")
    }
  }
}

