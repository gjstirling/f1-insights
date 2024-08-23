package repositories

import models.Drivers
import org.mongodb.scala.Document
import org.mongodb.scala.model._
import services.MyLogger
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DriversRepository @Inject()(dbConnection: MongoDbConnection[Drivers])(implicit ec: ExecutionContext) {

  private def updateAndUpsert(data: Seq[Drivers]): Seq[ReplaceOneModel[Drivers]] = {
    data.map { obj =>
      val filter = Filters.eq("full_name", obj.full_name)
      ReplaceOneModel(filter, obj, ReplaceOptions().upsert(true))
    }
  }

  def insertDrivers(data: Seq[Drivers]): Future[Unit] = {
    val bulkWrites = updateAndUpsert(data)
    MyLogger.info(s"[DriversRepository][insert]:")
    dbConnection.insert(bulkWrites)
  }

  def findAll(params: Map[String, String]): Future[Seq[Drivers]] = {
    val filter = Document()
    dbConnection.findAll(params, filter)
  }

}



