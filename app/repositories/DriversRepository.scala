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
    val filteredData = data.filter(_.team_name.isDefined)

    filteredData.map { obj =>
      val teamNameFilter = obj.team_name match {
        case Some(teamName) => Filters.eq("team_name", teamName)
      }

      val filter = Filters.and(
        Filters.eq("full_name", obj.full_name),
        teamNameFilter
      )

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



