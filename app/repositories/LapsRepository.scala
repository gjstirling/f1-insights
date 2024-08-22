package repositories

import config.MongoDbConnection
import models.Laps
import org.mongodb.scala._
import org.mongodb.scala.model._
import services.MyLogger
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LapsRepository @Inject()(dbConnection: MongoDbConnection[Laps])(implicit ec: ExecutionContext) {

  private def updateAndUpsert(data: Seq[Laps]): Seq[ReplaceOneModel[Laps]] = {
    data.map { lap =>
      ReplaceOneModel(Filters.empty(), lap, ReplaceOptions().upsert(true))
    }
  }

  def insertLaps(laps: Seq[Laps]): Future[Unit] = {
    val bulkWrites = updateAndUpsert(laps)
    MyLogger.info(s"[LapsRepository][insertLaps]:")
    dbConnection.insert(bulkWrites)
  }

  def findAll(params: Map[String, String]): Future[Seq[Laps]] = {
    val filter = Document("date_start" -> -1)
    dbConnection.findAll(params, filter)
  }
}

