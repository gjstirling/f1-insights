package main.scala.repositories

import org.mongodb.scala._
import main.scala.models.Event

import javax.inject.{Inject, Singleton}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import main.scala.config.MongoDbConnection
import main.scala.config.MyAppConfig._
import org.mongodb.scala.bson.codecs.Macros
import org.mongodb.scala.model._
import services.MyLogger

@Singleton
class EventRepository @Inject()(dbConnection: MongoDbConnection) {

  private lazy val codec = Macros.createCodecProvider[Event]()
  private lazy val collection = dbConnection.getCollection[Event](database, eventsCollection, codec)

  def sessionKeyFilter(events: Seq[Event]): Seq[ReplaceOneModel[Event]] = {
    // Prepare a list of bulk write operations session key filter
    events.map { event =>
      val filter = Filters.eq("session_key", event.session_key)
      val replaceOneModel = ReplaceOneModel(filter, event, ReplaceOptions().upsert(true))
      ReplaceOneModel(filter, event, ReplaceOptions().upsert(true))
    }
  }

  def insertEvents(events: Seq[Event]): Unit = {
    val bulkWrites = sessionKeyFilter(events)

    // perform bulk write to DB
    val bulkWriteResultFuture = collection.bulkWrite(bulkWrites).toFuture()
    // Wait for the bulk write operation to complete
    val bulkWriteResult = Await.result(bulkWriteResultFuture, Duration.Inf)
    // See the result in the console
    MyLogger.info(s"Inserted ${bulkWriteResult.getInsertedCount} events.")
  }
}