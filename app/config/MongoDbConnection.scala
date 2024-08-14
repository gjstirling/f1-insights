package config

import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala._

import javax.inject.Singleton
import scala.reflect.ClassTag

@Singleton
class MongoDbConnection {
  val password: String = scala.sys.env("DB_PASSWORD")
  val database: String = scala.sys.env("DATABASE")
  val connectionString = s"mongodb+srv://gstirling:$password@cluster0.zobrk9b.mongodb.net/";
  private val client: MongoClient = MongoClient(connectionString)

  def getCollection[T: ClassTag](db: String, collection: String, codecProvider: CodecProvider): MongoCollection[T] = {
    val codecRegistry = fromRegistries(fromProviders(codecProvider), DEFAULT_CODEC_REGISTRY)
    val db: MongoDatabase = client.getDatabase(database).withCodecRegistry(codecRegistry)
    db.getCollection[T](collection)
  }
}