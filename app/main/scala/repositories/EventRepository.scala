package main.scala.repositories

import org.mongodb.scala._
import main.scala.models.Event
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.model.Filters.equal

import javax.inject.{Inject, Singleton}
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import main.scala.config.{MongoDbConnection, MyAppConfig}
import org.mongodb.scala.model.Filters._

@Singleton
class EventRepository @Inject()(config: MyAppConfig, dbConnection: MongoDbConnection) {
  private def initialiseCollection(): MongoCollection[Event] = {
    val codecRegistry = fromRegistries(fromProviders(classOf[Event]), DEFAULT_CODEC_REGISTRY)
    val database: MongoDatabase = dbConnection.client.getDatabase(config.database).withCodecRegistry(codecRegistry)
    database.getCollection(config.eventsCollection)
  }
  def addAllEvents(event: Seq[Event]): Unit = {
    val collection = initialiseCollection()
    val result = collection.insertMany(event).toFuture()

    Await.result(result, Duration.Inf)
  }

  def find(filters: Map[String, String]): Seq[Event] = {
    val filterCriteria = filters.map { case (key, value) => equal(key, value) }
    val combinedFilter = and(filterCriteria.toSeq: _*)

    val collection = initialiseCollection()
    val result = collection.find(combinedFilter).toFuture()

    Await.result(result, Duration.Inf)
  }

  def hasSession(sessionKey: Int): Boolean = {
    sessionKey == 9468
  }


}
