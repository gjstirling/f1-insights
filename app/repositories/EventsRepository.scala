package repositories

import models.Event
import javax.inject.{Inject, Singleton}
import config.MongoDbConnection
import org.mongodb.scala.bson.codecs.Macros
import org.mongodb.scala._
import org.mongodb.scala.model._
import services.MyLogger
import scala.concurrent.ExecutionContext


@Singleton
class EventsRepository @Inject()(dbConnection: MongoDbConnection)(implicit ec: ExecutionContext) {
  val database = "f1insights"
  val eventsCollection = "events"

  private lazy val codec = Macros.createCodecProvider[Event]()
  private lazy val collection = dbConnection.getCollection[Event](database, eventsCollection, codec)


  private def sessionKeyFilter(events: Seq[Event]): Seq[ReplaceOneModel[Event]] = {
    events.map { event =>
      val filter = Filters.eq("session_key", event.session_key)
      ReplaceOneModel(filter, event, ReplaceOptions().upsert(true))
    }
  }

  def insert(events: Seq[Event]): Unit = {
    val bulkWrites = sessionKeyFilter(events)

    collection.bulkWrite(bulkWrites).toFuture().map { bulkWriteResult =>
      MyLogger.info(s"Bulk write result: ${bulkWriteResult.getMatchedCount} " +
        s"matched, ${bulkWriteResult.getUpserts.size} upserted.")
    }.recover {
      case ex: Throwable => MyLogger.info(s"Error during bulk write operation: ${ex.getMessage}")
    }
  }
}
