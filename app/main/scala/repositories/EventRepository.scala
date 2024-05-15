package main.scala.repositories

import org.mongodb.scala._
import main.scala.models.Event
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}

import javax.inject.{Inject, Singleton}
import scala.concurrent.Await
import scala.concurrent.duration.Duration

@Singleton
class EventRepository @Inject()() {
  private def initializeCollection(): MongoCollection[Event] = {
    val codecRegistry = fromRegistries(fromProviders(classOf[Event]), DEFAULT_CODEC_REGISTRY)
    val mongoClient: MongoClient = MongoClient()
    val database: MongoDatabase = mongoClient.getDatabase("mydb").withCodecRegistry(codecRegistry)
    database.getCollection("test")
  }

  def addOneEvent(event: Event): Unit = {
    val collection = initializeCollection()
    val result = collection.insertOne(event).toFuture()

    Await.result(result, Duration.Inf)
  }

  def addAllEvents(event: List[Event]): Unit = {
    val collection = initializeCollection()
    val result = collection.insertMany(event).toFuture()

    Await.result(result, Duration.Inf)
  }
}
