package main.scala.config

import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala._

import javax.inject.Singleton
import scala.reflect.ClassTag

@Singleton
class MongoDbConnection {
  private val client: MongoClient = MongoClient()

  def connect[T: ClassTag](db: String, collection: String, codecProvider: CodecProvider): MongoCollection[T] = {
    val codecRegistry = fromRegistries(fromProviders(codecProvider), DEFAULT_CODEC_REGISTRY)
    val database: MongoDatabase = client.getDatabase(db).withCodecRegistry(codecRegistry)
    database.getCollection[T](collection)
  }
}