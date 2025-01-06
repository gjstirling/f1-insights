package repositories

import .mockDriversList
import base.UnitSpec
import models.Drivers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.mongodb.scala.Document
import org.mongodb.scala.model.{Filters, ReplaceOneModel, ReplaceOptions}
import scala.concurrent.Future

class DriversRepositorySpec extends UnitSpec {

  val mockParams: Map[String, String] = Map("key1" -> "value1", "key2" -> "value2")
  val mockDatabase: MongoDbConnectionManager[Drivers] = mock[MongoDbConnectionManager[Drivers]]
  val repository = new DriversRepository(mockDatabase)
  val bulkWrites: Seq[ReplaceOneModel[Drivers]] = mockDriversList.map { obj =>
    val filter = Filters.eq("full_name", obj.full_name)
    ReplaceOneModel(filter, obj, ReplaceOptions().upsert(true))
  }

  "[DriversRepository][findAll]" should {

    "return a list of drivers" in {
      val filter = Document()
      when(mockDatabase.findAll(mockParams, filter)).thenReturn(Future.successful(mockDriversList))

      val resultFuture = repository.findAll(mockParams)

      resultFuture.map { result =>
        result shouldEqual mockDriversList
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
      when(mockDatabase.insert(any[Seq[ReplaceOneModel[Drivers]]])).thenReturn(Future.successful())
      val resultFuture = repository.insertDrivers(mockDriversList)

      resultFuture.map { _ =>
        verify(mockDatabase).insert(bulkWrites)
      }

    }

    "handle database insertion error" in {
      when(mockDatabase.insert(any[Seq[ReplaceOneModel[Drivers]]])).thenReturn(Future.failed(
        new Throwable("Failed to insert drivers into MongoDB")))
      val resultFuture = repository.insertDrivers(mockDriversList)

      resultFuture.map { _ =>
        verify(mockDatabase).insert(bulkWrites)
      }.recover {
        case ex =>
          fail(s"Expected successful completion, but got failure: ${ex.getMessage}")
      }

    }
  }
}
