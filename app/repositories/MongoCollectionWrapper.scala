package repositories

import org.bson.codecs.configuration.CodecProvider
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

  def findAll(filter: Document, order: Document): Future[Seq[T]] = {
    handleDbOperation(collection.find(filter).sort(order).toFuture(), "findAll")
  }

  def insert(bulkWrites: Seq[ReplaceOneModel[T]]): Future[Unit] = {
    handleDbOperation(collection.bulkWrite(bulkWrites).toFuture(), "insert").map(_ => ())
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
