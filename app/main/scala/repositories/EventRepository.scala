package main.scala.repositories

import org.mongodb.scala._
import main.scala.models.Event
import org.mongodb.scala.bson.codecs.Macros.createCodecProvider
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.model.Filters.equal

import javax.inject.{Inject, Singleton}
import scala.concurrent.Await
import scala.concurrent.duration.{Duration, DurationInt}
import main.scala.config.{MongoDbConnection, MyAppConfig}
import org.mongodb.scala.model.Filters._
import services.MyLogger

@Singleton
class EventRepository @Inject()(config: MyAppConfig, dbConnection: MongoDbConnection) {
  private def initialiseCollection(): MongoCollection[Event] = {
    val codecRegistry = fromRegistries(fromProviders(classOf[Event]), DEFAULT_CODEC_REGISTRY)
    val database: MongoDatabase = dbConnection.client.getDatabase(config.database).withCodecRegistry(codecRegistry)
    database.getCollection(config.eventsCollection)
  }

  def insertEvents(events: Seq[Event]): Unit = {
    val collection = initialiseCollection()
    // Find events currently stored in collection
    val sessionKeysToCheck = events.map(_.session_key)
    val query = in("session_key", sessionKeysToCheck: _*)
    val futureResults = collection.find(query).toFuture()
    val sessionKeysInDb = Await.result(futureResults, 10.seconds)

    // Filter list based on what is already stored in DB
    val existingSessionKeys = sessionKeysInDb.map(_.session_key).toSet
    val eventsToInsert = events.filterNot(event => existingSessionKeys.contains(event.session_key))

    if (eventsToInsert.nonEmpty) {
      val result = collection.insertMany(eventsToInsert).toFuture()
      Await.result(result, Duration.Inf)
    }
  }

  def find(filters: Map[String, String]): Seq[Event] = {
    val filterCriteria = filters.map { case (key, value) => equal(key, value) }
    val combinedFilter = and(filterCriteria.toSeq: _*)

    val collection = initialiseCollection()
    val result = collection.find(combinedFilter).toFuture()

    Await.result(result, Duration.Inf)
  }
}
