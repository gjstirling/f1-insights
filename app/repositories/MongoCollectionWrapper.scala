package repositories

import config.MyAppConfig.{connectionString, database}
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.{BsonInt32, BsonString}
import org.mongodb.scala.{Document, _}
import org.mongodb.scala.model.ReplaceOneModel
import services.MyLogger
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag
import scala.util.{Failure, Success}

@Singleton
class MongoCollectionWrapper[T: ClassTag] @Inject()(collectionName: String, codec: CodecProvider)(implicit ec: ExecutionContext) {

  private val db: MongoDatabase = MongoDbConnectionManager.getDatabase(codec)
  private val collection = db.getCollection[T](collectionName)

  def findAll(params: Map[String, Any], order: Document): Future[Seq[T]] = {
    val query = buildQuery(params)
    handleDbOperation(collection.find(query).sort(order).toFuture(), "findAll")
  }

  def insert(bulkWrites: Seq[ReplaceOneModel[T]]): Future[Unit] = {
    handleDbOperation(collection.bulkWrite(bulkWrites).toFuture(), "insert").map(_ => ())
  }

  private def buildQuery(params: Map[String, Any]): Document = {
    Document(params.map {
      case (key, value: String)  => key -> BsonString(value)
      case (key, value: Int)     => key -> BsonInt32(value)
      case (key, _)              => throw new IllegalArgumentException(s"Unsupported type for key $key")
    })
  }

  private def handleDbOperation[R](operation: => Future[R], operationName: String): Future[R] = {
    operation.transform {
      case Success(result) =>
        MyLogger.info(s"[$operationName]: Operation successful")
        Success(result)
      case Failure(exception) =>
        MyLogger.red(s"[$operationName]: Operation failed - ${exception.getMessage}")
        Failure(exception)
    }
  }
}
