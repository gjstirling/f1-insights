package base

import main.scala.config.MyAppConfig
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.mvc.{MessagesControllerComponents, Request}
import play.api.test.FakeRequest

trait ControllersSpecWithGuiceApp extends ControllersSpecBase with GuiceOneAppPerSuite {
  lazy val request: Request[_] = FakeRequest()

  implicit lazy val mockMcc: MessagesControllerComponents = app.injector.instanceOf[MessagesControllerComponents]
  implicit lazy val appConfig: MyAppConfig = app.injector.instanceOf[MyAppConfig]
}