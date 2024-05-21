package base

import org.scalatest.OptionValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import scala.concurrent.ExecutionContext
import scala.language.{implicitConversions, postfixOps}

trait UnitSpec extends AnyWordSpecLike with Matchers with OptionValues with GuiceOneAppPerSuite {
  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
}