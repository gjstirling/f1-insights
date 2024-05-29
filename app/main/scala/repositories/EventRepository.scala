package main.scala.repositories

import org.mongodb.scala._
import main.scala.models.Event
import org.mongodb.scala.model.Filters.equal

import javax.inject.{Inject, Singleton}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import main.scala.config.{MongoDbConnection, MyAppConfig}
import org.mongodb.scala.bson.codecs.Macros
import org.mongodb.scala.bson.conversions
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model._
import services.MyLogger

@Singleton
class EventRepository @Inject()(config: MyAppConfig, dbConnection: MongoDbConnection) {

  private lazy val codec = Macros.createCodecProvider[Event]()
  private lazy val collection = dbConnection.getCollection[Event](config.database, config.eventsCollection, codec)

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
