package repositories

import models.Event
import javax.inject.{Inject, Singleton}
import org.mongodb.scala._
import org.mongodb.scala.model._
import services.MyLogger
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EventsRepository @Inject()(dbConnection: MongoDbConnection[Event])(implicit ec: ExecutionContext) {

  def insertEvents(events: Seq[Event]): Future[Unit] = {
    MyLogger.info(s"[EventsRepository][insert]:")

    val bulkWrites = events.map { obj =>
      val filter = Filters.eq("session_key", obj.session_key)
      ReplaceOneModel(filter, obj, ReplaceOptions().upsert(true))
    }

    dbConnection.insert(bulkWrites).recover {
      case ex: Throwable =>
        MyLogger.red(s"Failed to insert events")
    }
  }

  def findAll(params: Map[String, String]): Future[Seq[Event]] = {
    MyLogger.info(s"[EventsRepository][findAll]:")

    val filter = Document("date_start" -> -1)
    dbConnection.findAll(params, filter).recover {
      case ex: Throwable =>
        MyLogger.red(s"Failed to find events, $ex")
        Seq.empty[Event]
    }
  }
}
