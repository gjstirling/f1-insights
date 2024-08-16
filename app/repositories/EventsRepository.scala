package repositories

import models.Event
import javax.inject.{Inject, Singleton}
import config.{MongoDbConnection, MyAppConfig}
import org.mongodb.scala.bson.codecs.Macros
import org.mongodb.scala._
import org.mongodb.scala.model._
import services.MyLogger
import scala.concurrent.{ExecutionContext, Future}
import org.bson.codecs.configuration.CodecProvider


@Singleton
class EventsRepository @Inject()(dbConnection: MongoDbConnection)(implicit ec: ExecutionContext) extends BaseRepository[Event](dbConnection, MyAppConfig.eventsCollection, EventsRepository.codec) {

  private def updateAndUpsert(data: Seq[Event]): Seq[ReplaceOneModel[Event]] = {
    data.map { obj =>
      val filter = Filters.eq("session_key", obj.session_key)
      ReplaceOneModel(filter, obj, ReplaceOptions().upsert(true))
    }
  }

  def insertEvents(events: Seq[Event]): Future[Unit] = {
    val bulkWrites = updateAndUpsert(events)
    MyLogger.info(s"[EventsRepository][insert]:")
    super.insert(bulkWrites)
  }

  def findAll(params: Map[String, String]): Future[Seq[Event]] = {
    val filter = Document("date_start" -> -1)
    super.findAll(params, filter)
  }
}

object EventsRepository {
  val codec: CodecProvider = Macros.createCodecProvider[Event]()
}
