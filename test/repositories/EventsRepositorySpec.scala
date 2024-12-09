package repositories

import base.TestData.mockEventList
import base.UnitSpec
import org.mockito.Mockito._
import org.mongodb.scala.model.{Filters, ReplaceOneModel, ReplaceOptions}
import org.mongodb.scala.Document
import org.mockito.ArgumentMatchers.any
import scala.concurrent.Future
import models.Event

class EventsRepositorySpec extends UnitSpec {

  val mockParams: Map[String, String] = Map("key1" -> "value1", "key2" -> "value2")
  val mockDatabase: MongoDbConnectionManager[Event] = mock[MongoDbConnectionManager[Event]]
  val repository = new EventsRepository(mockDatabase)
  val bulkWrites: Seq[ReplaceOneModel[Event]] = mockEventList.map { obj =>
    val filter = Filters.eq("session_key", obj.session_key)
    ReplaceOneModel(filter, obj, ReplaceOptions().upsert(true))
  }

  "[EventsRepository][findAll]" should {

    "return a list of events" in {
      val filter = Document("date_start" -> -1)
      when(mockDatabase.findAll(mockParams, filter)).thenReturn(Future.successful(mockEventList))

      val resultFuture = repository.findAll(mockParams)

      resultFuture.map { result =>
        result shouldEqual mockEventList
      }
    }

    "return an empty list when database query fails" in {
      val filter = Document("date_start" -> -1)
      when(mockDatabase.findAll(mockParams, filter)).thenReturn(Future.failed(new Throwable()))

      val resultFuture = repository.findAll(mockParams)

      resultFuture.map { result =>
        result shouldEqual Seq.empty
      }
    }
  }

  "[EventsRepository][insertEvents]" should {
    "successfully insert events" in {
      when(mockDatabase.insert(any[Seq[ReplaceOneModel[Event]]])).thenReturn(Future.successful())
      val resultFuture = repository.insertEvents(mockEventList)

      resultFuture.map { _ =>
        verify(mockDatabase).insert(bulkWrites)
      }

    }

    "handle database insertion error" in {
      when(mockDatabase.insert(any[Seq[ReplaceOneModel[Event]]])).thenReturn(Future.failed(new Throwable("Failed to insert events")))
      val resultFuture = repository.insertEvents(mockEventList)

      resultFuture.map { _ =>
        verify(mockDatabase).insert(bulkWrites)
      }.recover {
        case ex =>
          fail(s"Expected successful completion, but got failure: ${ex.getMessage}")
      }

    }
  }
}
