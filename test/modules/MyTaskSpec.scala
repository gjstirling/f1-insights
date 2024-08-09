package modules

import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.inject.{Binding, Module}
import play.api.{Application, Configuration, Environment}
import play.api.inject.guice._
import tasks.MyTask

class MyTaskSpec extends PlaySpec with GuiceOneAppPerSuite {

  override def fakeApplication(): Application = {
    new GuiceApplicationBuilder()
      .bindings(new Module {
        override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
          Seq(
            bind[MyTask].toSelf.eagerly(),
          )
        }
      })
      .build()
  }

  "MyTask" should {
    "be initialized" in {
      val myDriverTask = app.injector.instanceOf[MyTask]
      myDriverTask must not be null
    }
  }
}
