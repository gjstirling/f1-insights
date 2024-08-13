package repositories

import config.MongoDbConnection
import config.MyAppConfig.driverCollection
import models.Drivers
import org.mongodb.scala._
import org.mongodb.scala.bson.codecs.Macros
import org.mongodb.scala.model._
import services.MyLogger

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DriversRepository @Inject()(dbConnection: MongoDbConnection)(implicit ec: ExecutionContext) {
  private lazy val codec = Macros.createCodecProvider[Drivers]()
  private lazy val collection = dbConnection.getCollection[Drivers](dbConnection.database, driverCollection, codec)

  private def sessionKeyFilter(drivers: Seq[Drivers]): Seq[ReplaceOneModel[Drivers]] = {
    drivers.map { driver =>
      val filter = Filters.eq("full_name", driver.full_name)
      ReplaceOneModel(filter, driver, ReplaceOptions().upsert(true))
    }
  }

  def insert(drivers: Seq[Drivers]): Future[Unit] = {
    val bulkWrites = sessionKeyFilter(drivers)

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
