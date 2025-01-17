package repositories

import models.Event

import javax.inject.{Inject, Singleton}
import org.mongodb.scala._
import org.mongodb.scala.bson.BsonString
import org.mongodb.scala.model._
import services.MyLogger

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EventsRepository @Inject()(dbConnection: MongoCollectionWrapper[Event])(implicit ec: ExecutionContext) {

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
    val query =  Document(params.map {
      case (key, value: String)  => key -> BsonString(value)
      case (key, _)              => throw new IllegalArgumentException(s"Unsupported type for key $key")
    })

    val order = Document("date_start" -> -1)
    dbConnection.findAll(query, order).recover {
      case ex: Throwable =>
        MyLogger.red(s"Failed to find events, $ex")
        Seq.empty[Event]
    }
  }

  def getSessionKeys(params: Map[String, String]): Future[Seq[Int]] = {
    findAll(params).map { events =>
      events.map(_.session_key)
    }.recover {
      case ex: Throwable =>
        MyLogger.red(s"Failed to retrieve session keys, $ex")
        Seq.empty[Int]
    }
  }
}
