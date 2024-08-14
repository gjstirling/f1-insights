package repositories

import config.MongoDbConnection
import org.bson.codecs.configuration.CodecProvider
import org.mongodb.scala.MongoCollection

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag

abstract class BaseRepository[T: ClassTag] @Inject()(
                                                      dbConnection: MongoDbConnection,
                                                      collectionName: String,
                                                      codec: CodecProvider
                                                    )(implicit ec: ExecutionContext) {

  protected lazy val collection: MongoCollection[T] =
    dbConnection.getCollection[T](dbConnection.database, collectionName, codec)

}