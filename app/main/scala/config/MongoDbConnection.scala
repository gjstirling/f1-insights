package main.scala.config

import main.scala.models.Event
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala._
import org.mongodb.scala.bson.codecs.Macros

import javax.inject.Singleton

@Singleton
class MongoDbConnection {
  private val client: MongoClient = MongoClient()

  def connect(db: String, collection: String): MongoCollection[Event] = {
    val codec = Macros.createCodecProvider[Event]()

    val codecRegistry = fromRegistries(fromProviders(codec), DEFAULT_CODEC_REGISTRY)
    val database: MongoDatabase = client.getDatabase(db).withCodecRegistry(codecRegistry)
    database.getCollection[Event](collection)
  }
}