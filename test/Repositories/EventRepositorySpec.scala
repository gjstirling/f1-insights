import org.scalatest.matchers.should.Matchers._
import org.scalatest.concurrent.ScalaFutures._
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.{Filters, ReplaceOneModel, ReplaceOptions}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import main.scala.models.Event
import main.scala.config.{MyAppConfig}
import main.scala.config.MongoDbConnection

class EventRepositorySpec extends org.scalatest.freespec.AnyFreeSpec with MockitoSugar {

  "EventRepository" - {
    "insertEvents" - {
      "should insert events into the database" in {
          //TODO
      }

      "find" - {
        "should find events based on given filters" in {
          //TODO
        }
      }
    }
  }
}
