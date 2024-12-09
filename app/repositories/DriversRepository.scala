package repositories

import models.Drivers
import org.mongodb.scala.Document
import org.mongodb.scala.model._
import services.MyLogger
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DriversRepository @Inject()(dbConnection: MongoCollectionWrapper[Drivers])(implicit ec: ExecutionContext) {

  def insertDrivers(data: Seq[Drivers]): Future[Unit] = {
    val bulkWrites = updateAndUpsert(data)
    MyLogger.info(s"[DriversRepository][insertDrivers]: Starting bulk insert for ${data.size} drivers")
    dbConnection.insert(bulkWrites).map { _ =>
      MyLogger.info(s"[DriversRepository][insertDrivers]: Bulk insert completed")
    }
  }

  def findAll(params: Map[String, String]): Future[Seq[Drivers]] = {
    MyLogger.info(s"[DriversRepository][findAll]: Fetching drivers with params: $params")
    dbConnection.findAll(params, Document())
  }

  private def updateAndUpsert(data: Seq[Drivers]): Seq[ReplaceOneModel[Drivers]] = {
    data.filter(_.team_name.isDefined).map { obj =>
      val filter = Filters.and(
        Filters.eq("full_name", obj.full_name),
        Filters.eq("team_name", obj.team_name.get)
      )
      ReplaceOneModel(filter, obj, ReplaceOptions().upsert(true))
    }
  }
}




