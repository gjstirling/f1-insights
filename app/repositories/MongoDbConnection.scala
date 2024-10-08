package repositories

import config.MyAppConfig.{connectionString, database}
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.{Document, _}
import org.mongodb.scala.model.ReplaceOneModel
import services.MyLogger

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag
@Singleton
class MongoDbConnection[T: ClassTag] @Inject() (collectionString: String, codec: CodecProvider) (implicit ec: ExecutionContext) {

  private val collection = {
    val codecRegistry = fromRegistries(fromProviders(codec), DEFAULT_CODEC_REGISTRY)
    val db: MongoDatabase = MongoClient(connectionString).getDatabase(database).withCodecRegistry(codecRegistry)
    db.getCollection[T](collectionString)
  }

  def findAll(params: Map[String, String], order: Document): Future[Seq[T]] = {
    val query: Document = Document(params.map {
      case (key, value) => key -> value
    }.toSeq)

    collection
      .find(query)
      .sort(order)
      .toFuture()
  }

  def insert(bulkWrites: Seq[ReplaceOneModel[T]]): Future[Unit] = {
    collection.bulkWrite(bulkWrites).toFuture().map { bulkWriteResult =>
      MyLogger.info(s"Bulk write result: " +
        s"${bulkWriteResult.getMatchedCount} matched, " +
        s"${bulkWriteResult.getUpserts.size} upserted")
    }.recover {
      case ex: Throwable =>
        MyLogger.red(s"Error during bulk write operation: ${ex.getMessage}")
    }.map(_ => ())
  }

}