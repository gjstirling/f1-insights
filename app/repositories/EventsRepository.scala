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

  private def updateAndUpsert(events: Seq[Event], paramKey: String): Seq[ReplaceOneModel[Event]] = {
    events.map { event =>
      val filter = Filters.eq(paramKey, event.session_key)
      ReplaceOneModel(filter, event, ReplaceOptions().upsert(true))
    }
  }

  def insertEvents(events: Seq[Event]): Future[Unit] = {
    val bulkWrites = updateAndUpsert(events, "session_key")
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
