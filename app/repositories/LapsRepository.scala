package repositories

import models.QualifyingLaps
import org.mongodb.scala.model.{Filters, ReplaceOneModel, ReplaceOptions}
import services.MyLogger

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LapsRepository @Inject()(dbConnection: MongoDbConnection[QualifyingLaps])(implicit ec: ExecutionContext) {

  private def updateAndUpsert(data: Seq[QualifyingLaps]): Seq[ReplaceOneModel[QualifyingLaps]] = {
    data.map { obj =>
      val filter = Filters.eq("", obj.session_key)

      ReplaceOneModel(filter, obj, ReplaceOptions().upsert(true))
    }
  }

  def insert(data: Seq[QualifyingLaps]): Future[Unit] = {
    MyLogger.info("DATA HAS ARRIVED:   " + data)

    val bulkWrites = updateAndUpsert(data)

    dbConnection.insert(bulkWrites).recover {
      case ex: Throwable =>
        MyLogger.red(s"Failed to insert laps")
    }
  }

}



