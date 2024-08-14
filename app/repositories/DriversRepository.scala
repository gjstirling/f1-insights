package repositories

import config.{MongoDbConnection, MyAppConfig}
import models.Drivers
import org.bson.codecs.configuration.CodecProvider
import org.mongodb.scala.Document
import org.mongodb.scala.bson.codecs.Macros
import org.mongodb.scala.model._
import services.MyLogger
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DriversRepository @Inject()(
                                   dbConnection: MongoDbConnection
                                 )(implicit ec: ExecutionContext)
  extends BaseRepository[Drivers](dbConnection, MyAppConfig.driverCollection, DriversRepository.codec) {

  private def updateAndUpsert(drivers: Seq[Drivers]): Seq[ReplaceOneModel[Drivers]] = {
    drivers.map { driver =>
      val filter = Filters.eq("full_name", driver.full_name)
      ReplaceOneModel(filter, driver, ReplaceOptions().upsert(true))
    }
  }

  def insertDrivers(drivers: Seq[Drivers]): Future[Unit] = {
    val bulkWrites = updateAndUpsert(drivers)
    MyLogger.info(s"[DriversRepository][insert]:")
    super.insert(bulkWrites)
  }

  def findAll(params: Map[String, String]): Future[Seq[Drivers]] = {
    val filter = Document()
    super.findAll(params, filter)
  }
}

object DriversRepository {
  val codec: CodecProvider = Macros.createCodecProvider[Drivers]()
}



