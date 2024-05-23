package main.scala.repositories

import org.mongodb.scala._
import main.scala.models.Event
import org.mongodb.scala.bson.codecs.Macros.createCodecProvider
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.model.Filters.equal

import javax.inject.{Inject, Singleton}
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import main.scala.config.{MongoDbConnection, MyAppConfig}
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model._
import services.MyLogger

@Singleton
class EventRepository @Inject()(config: MyAppConfig, dbConnection: MongoDbConnection) {
  private def initialiseEventsCollection(): MongoCollection[Event] = {
    val codecRegistry = fromRegistries(fromProviders(classOf[Event]), DEFAULT_CODEC_REGISTRY)
    val database: MongoDatabase = dbConnection.client.getDatabase(config.database).withCodecRegistry(codecRegistry)
    database.getCollection(config.eventsCollection)
  }

  def insertEvents(events: Seq[Event]): Unit = {
    val collection = initialiseEventsCollection()
    // Prepare a list of bulk write operations
    val bulkWrites = events.map { event =>
      val filter = Filters.eq("session_key", event.session_key)
      val replaceOneModel = ReplaceOneModel(filter, event, ReplaceOptions().upsert(true))
      ReplaceOneModel(filter, event, ReplaceOptions().upsert(true))
    }
    // perform bulk write to DB
    val bulkWriteResultFuture = collection.bulkWrite(bulkWrites).toFuture()
    // Wait for the bulk write operation to complete
    val bulkWriteResult = Await.result(bulkWriteResultFuture, Duration.Inf)
    // See the result in the console
    MyLogger.info(s"Inserted ${bulkWriteResult.getInsertedCount} events.")
  }

  def find(filters: Map[String, String]): Seq[Event] = {
    val filterCriteria = filters.map { case (key, value) => equal(key, value) }
    val combinedFilter = and(filterCriteria.toSeq: _*)

    val collection = initialiseEventsCollection()
    val result = collection.find(combinedFilter).toFuture()

    Await.result(result, Duration.Inf)
  }
}
