package repositories

import config.MongoDbConnection
import models.Drivers
import org.mongodb.scala.Document
import org.mongodb.scala.model._
import services.MyLogger
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DriversRepository @Inject()(dbConnection: MongoDbConnection[Drivers])(implicit ec: ExecutionContext) {

  private def updateAndUpsert(drivers: Seq[Drivers]): Seq[ReplaceOneModel[Drivers]] = {
    drivers.map { driver =>
      val filter = Filters.eq("full_name", driver.full_name)
      ReplaceOneModel(filter, driver, ReplaceOptions().upsert(true))
    }
  }

  def insertDrivers(drivers: Seq[Drivers]): Future[Unit] = {
    val bulkWrites = updateAndUpsert(drivers)
    MyLogger.info(s"[DriversRepository][insert]:")
    dbConnection.insert(bulkWrites)
  }

  def findAll(params: Map[String, String]): Future[Seq[Drivers]] = {
    val filter = Document()
    dbConnection.findAll(params, filter)
  }

}



