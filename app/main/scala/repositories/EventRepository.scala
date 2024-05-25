package main.scala.repositories

import org.mongodb.scala._
import main.scala.models.Event
import org.mongodb.scala.bson.codecs.Macros.createCodecProvider
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.model.Filters.equal

import javax.inject.{Inject, Singleton}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import main.scala.config.{MongoDbConnection, MyAppConfig}
import org.mongodb.scala.bson.conversions
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model._
import services.MyLogger

@Singleton
class EventRepository @Inject()(config: MyAppConfig, dbConnection: MongoDbConnection) {

  def insertEvents(events: Seq[Event]): Unit = {
    val collection = dbConnection.connect(config.database, config.eventsCollection)
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

  def find(filters: Map[String, String]): Future[Seq[Event]] = {
    val collection = dbConnection.connect(config.database, config.eventsCollection)


    val filterCriteria = filters.map { case (key, value) => equal(key, value) }
    val combinedFilter: conversions.Bson = and(filterCriteria.toSeq: _*)

    collection.find(combinedFilter).toFuture()
  }
}
