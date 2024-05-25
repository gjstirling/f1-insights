package repositories

import base.ControllersSpecWithGuiceApp
import base.TestData._
import main.scala.models.Event
import main.scala.config.{MongoDbConnection, MyAppConfig}
import main.scala.repositories.EventRepository
import org.mongodb.scala.MongoCollection
import org.scalatestplus.mockito.MockitoSugar


class EventRepositorySpec extends ControllersSpecWithGuiceApp with MockitoSugar {

  // Mocking dependencies
  val mockMyAppConfig: MyAppConfig = mock[MyAppConfig]
  val mockDbConnection: MongoDbConnection = mock[MongoDbConnection]
  val mockCollection: MongoCollection[Event] = mock[MongoCollection[Event]]

  val repository = new EventRepository(
    mockMyAppConfig, mockDbConnection
  )

  "EventRepository" should {
    "find events based on given filters" in {
      // Mocking configuration values
    }
  }
}
