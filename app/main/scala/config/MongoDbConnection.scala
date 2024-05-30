package main.scala.config

import com.mongodb.{ServerApi, ServerApiVersion}
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala._

import javax.inject.Singleton
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.reflect.ClassTag
import scala.util.Using

@Singleton
class MongoDbConnection {

  // Retrieve the password from the environment variables
  private val password: String = sys.env.getOrElse("PASSWORD", throw new IllegalStateException("PASSWORD environment variable is not set"))

  val connectionString = s"mongodb+srv://graemejstirling:$password@mycluster.k53nho7.mongodb.net/?retryWrites=true&w=majority&appName=MyCluster"  // Construct a ServerApi instance using the ServerApi.builder() method
  val serverApi: ServerApi = ServerApi.builder.version(ServerApiVersion.V1).build()
  val settings: MongoClientSettings = MongoClientSettings
    .builder()
    .applyConnectionString(ConnectionString(connectionString))
    .serverApi(serverApi)
    .build()

  // Mongo DB Atlas
  private val client: MongoClient = MongoClient(settings)
  // local db
  //private val client: MongoClient = MongoClient()


  def getCollection[T: ClassTag](db: String, collection: String, codecProvider: CodecProvider): MongoCollection[T] = {
    val codecRegistry = fromRegistries(fromProviders(codecProvider), DEFAULT_CODEC_REGISTRY)
    val database: MongoDatabase = client.getDatabase(db).withCodecRegistry(codecRegistry)
    database.getCollection[T](collection)
  }
}