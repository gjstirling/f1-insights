package repositories

import models.SessionData
import org.mongodb.scala.Document
import services.MyLogger

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import org.mongodb.scala.model._
import play.api.mvc.Filter

@Singleton
class SessionDataRepository @Inject()(dbConnection: MongoCollectionWrapper[SessionData])(implicit ec: ExecutionContext) {

  def insertData(data: Seq[SessionData]): Future[Unit] = {
    val replaceModels = data.map { obj =>
      ReplaceOneModel(
        Filters.and(
          Filters.eq("session_key", obj.session_key),
          Filters.eq("date", obj.date)
        ),
        obj,
        ReplaceOptions().upsert(true)
      )
    }

    dbConnection.insert(replaceModels).map { _ =>
      MyLogger.info(s"[SessionDataRepository][insertData]: Bulk upsert completed with ${replaceModels.size} records.")
    }.recover {
      case ex: Throwable =>
        MyLogger.red(s"[SessionDataRepository][insertData]: Failed to upsert data: ${ex.getMessage}")
    }
  }

    def getBySessionKey(sessionKey: Int): Future[Seq[SessionData]] = {
      val params: Map[String, Any] = Map(
        "session_key" -> sessionKey,
        "message" -> "GREEN LIGHT - PIT EXIT OPEN",
      )

      dbConnection.findAll(params, Document()).recover {
        case ex: Throwable =>
          MyLogger.red(s"Failed to find events, $ex")
          Seq.empty[SessionData]
      }
    }
}






