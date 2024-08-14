package repositories

import config.{MongoDbConnection, MyAppConfig}
import models.Drivers
import org.bson.codecs.configuration.CodecProvider
import org.mongodb.scala.bson.codecs.Macros
import org.mongodb.scala.model._
import services.MyLogger

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DriversRepository @Inject()(
                                   dbConnection: MongoDbConnection
                                 )(implicit ec: ExecutionContext)
  extends BaseRepository[Drivers](dbConnection, MyAppConfig.driverCollection, DriversRepository.codec) {

  private def updateAndUpsert(drivers: Seq[Drivers]): Seq[ReplaceOneModel[Drivers]] = {
    drivers.map { driver =>
      val filter = Filters.eq("full_name", driver.full_name)
      ReplaceOneModel(filter, driver, ReplaceOptions().upsert(true))
    }
  }

  def insert(drivers: Seq[Drivers]): Future[Unit] = {
    val bulkWrites = updateAndUpsert(drivers)

    collection.bulkWrite(bulkWrites).toFuture().map { bulkWriteResult =>
      MyLogger.info(s"[DriversRepository][insert]: Bulk write result: " +
        s"${bulkWriteResult.getMatchedCount} matched, " +
        s"${bulkWriteResult.getUpserts.size} upserted")
    }.recover {
      case ex: Throwable =>
        MyLogger.red(s"Error during bulk write operation: ${ex.getMessage}")
    }.map(_ => ())
  }
}

object DriversRepository {
  val codec: CodecProvider = Macros.createCodecProvider[Drivers]()
}



