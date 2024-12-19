package repositories

import config.MyAppConfig.{connectionString, database}
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries, fromCodecs}
import org.bson.codecs.jsr310.InstantCodec
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.{MongoClient, MongoDatabase}

object MongoDbConnectionManager {
  private lazy val client: MongoClient = MongoClient(connectionString)

  def getDatabase(codecProvider: CodecProvider): MongoDatabase = {
    val codecRegistry = fromRegistries(
      fromProviders(codecProvider),
      fromCodecs(new InstantCodec()),
      DEFAULT_CODEC_REGISTRY
    )
    client.getDatabase(database).withCodecRegistry(codecRegistry)
  }

  def close(): Unit = {
    client.close()
  }
}
