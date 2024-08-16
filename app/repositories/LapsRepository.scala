package repositories

import config.{MongoDbConnection, MyAppConfig}
import models.Laps
import org.bson.codecs.configuration.CodecProvider
import org.mongodb.scala._
import org.mongodb.scala.bson.codecs.Macros
import org.mongodb.scala.model._
import services.MyLogger

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class LapsRepository @Inject()(dbConnection: MongoDbConnection)(implicit ec: ExecutionContext) extends BaseRepository[Laps](dbConnection, MyAppConfig.lapsCollection, LapsRepository.codec) {

  private def updateAndUpsert(data: Seq[Laps]): Seq[ReplaceOneModel[Laps]] = {
    data.map { lap =>
      ReplaceOneModel(Filters.empty, lap, ReplaceOptions().upsert(true))
    }
  }

  def insertLaps(laps: Seq[Laps]): Future[Unit] = {
    val bulkWrites = updateAndUpsert(laps)
    MyLogger.info(s"[LapsRepository][insertLaps]:")
    super.insert(bulkWrites)
  }

  def findAll(params: Map[String, String]): Future[Seq[Laps]] = {
    val filter = Document("date_start" -> -1)
    super.findAll(params, filter)
  }
}

object LapsRepository {
  val codec: CodecProvider = Macros.createCodecProvider[Laps]()
}
