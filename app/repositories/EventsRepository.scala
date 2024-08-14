package repositories

import models.Event
import javax.inject.{Inject, Singleton}
import config.{MongoDbConnection, MyAppConfig}
import org.mongodb.scala.bson.codecs.Macros
import org.mongodb.scala._
import org.mongodb.scala.model._
import services.MyLogger
import scala.concurrent.{ExecutionContext, Future}
import org.bson.codecs.configuration.CodecProvider


@Singleton
class EventsRepository @Inject()(dbConnection: MongoDbConnection)(implicit ec: ExecutionContext) extends BaseRepository[Event](dbConnection, MyAppConfig.eventsCollection, EventsRepository.codec) {

  private def updateAndUpsert(events: Seq[Event], paramKey: String): Seq[ReplaceOneModel[Event]] = {
    events.map { event =>
      val filter = Filters.eq(paramKey, event.session_key)
      ReplaceOneModel(filter, event, ReplaceOptions().upsert(true))
    }
  }

  def insert(events: Seq[Event]): Unit = {
    val bulkWrites = updateAndUpsert(events, "session_key")

    collection.bulkWrite(bulkWrites).toFuture().map { bulkWriteResult =>
      MyLogger.info(s"[EventRepository][insert]: Bulk write result: ${bulkWriteResult.getMatchedCount} " +
        s"matched, ${bulkWriteResult.getUpserts.size} upserted.")
    }.recover {
      case ex: Throwable => MyLogger.info(s"Error during bulk write operation: ${ex.getMessage}")
    }
  }

  def findAll(params: Map[String, String]): Future[Seq[Event]] = {
    val query: Document = Document(params.map {
      case (key, value) => key -> value
    }.toSeq)
    val order: Document = Document("date_start" -> -1)

    collection
      .find(query)
      .sort(order)
      .toFuture()
  }
}

object EventsRepository {
  val codec: CodecProvider = Macros.createCodecProvider[Event]()
}
