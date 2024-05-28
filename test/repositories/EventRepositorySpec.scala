package repositories

import main.scala.models.Event
import main.scala.repositories.EventRepository
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.{Filters, ReplaceOneModel, ReplaceOptions}
import org.scalatestplus.mockito.MockitoSugar
import main.scala.config.{MongoDbConnection, MyAppConfig}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import base.TestData.sampleEvents

class EventRepositorySpec extends AnyWordSpec with Matchers with MockitoSugar {

  // Mocking dependencies
  val mockMyAppConfig: MyAppConfig = mock[MyAppConfig]
  val mockDbConnection: MongoDbConnection = mock[MongoDbConnection]
  val mockCollection: MongoCollection[Event] = mock[MongoCollection[Event]]

  val repository = new EventRepository(
    mockMyAppConfig, mockDbConnection
  )

// Methods Used as no correct equality check even when exactly the same
  private def replaceOptionsEqualityCheck(actual: ReplaceOptions, expected: ReplaceOptions): Boolean = {
    actual.isUpsert == expected.isUpsert &&
      actual.getBypassDocumentValidation == expected.getBypassDocumentValidation &&
      actual.getCollation == expected.getCollation &&
      actual.getHint == expected.getHint &&
      actual.getHintString == expected.getHintString &&
      actual.getComment == expected.getComment &&
      actual.getLet == expected.getLet
  }

  private def replaceOneModelEqualityCheck[T](actual: ReplaceOneModel[T], expected: ReplaceOneModel[T]): Boolean = {
    val filterMatch = actual.getFilter.toBsonDocument == expected.getFilter.toBsonDocument
    val replacementMatch = actual.getReplacement == expected.getReplacement
    val optionsMatch = replaceOptionsEqualityCheck(actual.getReplaceOptions, expected.getReplaceOptions)

    filterMatch && replacementMatch && optionsMatch
  }

  "[EventRepository][SessionKeyFilter]" should {
    "Return an empty list when no events are given" in {
      val act = repository.sessionKeyFilter(List.empty)
      act shouldBe List.empty
    }

    "Return the list with ReplaceOneModel Filter" in {
      val expected = List(
        ReplaceOneModel(
          Filters.eq("session_key", sampleEvents.head.session_key),
          sampleEvents.head,
          ReplaceOptions().upsert(true)
        )
      )

      val act = repository.sessionKeyFilter(sampleEvents)
      act.zip(expected).forall { case (a, e) => replaceOneModelEqualityCheck(a, e) } shouldBe true
    }
  }

  // Unit tests for .find .insertEvents would be mocked throughout so will need to be tested via integration test
}
