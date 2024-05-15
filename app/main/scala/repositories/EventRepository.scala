package main.scala.repositories

import org.mongodb.scala._
import org.mongodb.scala.result.InsertOneResult
import main.scala.models.Event
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromRegistries, fromProviders}
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object EventRepository extends App {

  def apply(): Unit = {
    val codecRegistry = fromRegistries(fromProviders(classOf[Event]), DEFAULT_CODEC_REGISTRY )

    val mongoClient: MongoClient = MongoClient()
    val database: MongoDatabase = mongoClient.getDatabase("mydb").withCodecRegistry(codecRegistry)
    val collection: MongoCollection[Event] = database.getCollection("test")

    val event = Event(
      location = "New York",
      session_name = "Tech Conference",
      date_start = "2024-05-15",
      gmt_offset = "-0400",
      session_key = 123,
      meeting_key = 456,
      year = 2024
    )

    println("Collection: " + collection)
    println("Inserting event: " + event)

    val result = collection.insertOne(event).toFuture()
    Await.result(result, Duration.Inf) // Wait for the result to complete

    println("Insertion result: " + result)
  }

  apply()
}