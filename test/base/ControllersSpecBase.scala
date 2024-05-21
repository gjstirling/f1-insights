package base

import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status
import play.api.mvc.Results


trait ControllersSpecBase  extends UnitSpec with ScalaFutures with MockitoSugar with BeforeAndAfterEach with Results with Status {
  // Test info ??
}
