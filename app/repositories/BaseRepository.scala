package repositories

import config.MongoDbConnection
import org.bson.codecs.configuration.CodecProvider
import org.mongodb.scala.model.ReplaceOneModel
import org.mongodb.scala.{Document, MongoCollection}
import services.MyLogger
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

abstract class BaseRepository[T: ClassTag] @Inject()(
                                                      dbConnection: MongoDbConnection,
                                                      collectionName: String,
                                                      codec: CodecProvider
                                                    )(implicit ec: ExecutionContext) {

  protected lazy val collection: MongoCollection[T] =
    dbConnection.getCollection[T](dbConnection.database, collectionName, codec)

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