package base

import main.scala.config.MyAppConfig
import main.scala.repositories.{EventRepository, F1OpenApi}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.mvc.{MessagesControllerComponents, Request}
import play.api.test.FakeRequest
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.ExecutionContext
import scala.language.{implicitConversions, postfixOps}

trait ControllersSpecWithGuiceApp extends ControllersSpecBase with GuiceOneAppPerSuite {
  lazy val request: Request[_] = FakeRequest()

  implicit lazy val mockMcc: MessagesControllerComponents = app.injector.instanceOf[MessagesControllerComponents]
  implicit lazy val appConfig: MyAppConfig = app.injector.instanceOf[MyAppConfig]
}

trait ControllersSpecBase  extends UnitSpec with MockitoSugar with Status {
  // Store mocks
  val mockEventRepository: EventRepository = mock[EventRepository]
  val mockF1OpenApiController: F1OpenApi = mock[F1OpenApi]
  val mockMyAppConfig: MyAppConfig = mock[MyAppConfig]
}
trait UnitSpec extends AnyWordSpecLike with Matchers with GuiceOneAppPerSuite {
  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
}
