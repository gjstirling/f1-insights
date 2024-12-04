package repositories

import models.Laps
import org.mongodb.scala.Document
import org.mongodb.scala.model.{Filters, ReplaceOneModel, ReplaceOptions}
import services.MyLogger

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LapsRepository @Inject()(dbConnection: MongoDbConnection[Laps])(implicit ec: ExecutionContext) {

  private def updateAndUpsert(data: Seq[Laps]): Seq[ReplaceOneModel[Laps]] = {
    data.map { obj =>
      val filter = Filters.and(
        Filters.eq("driver_number", obj.driver_number),
        Filters.eq("lap_number", obj.lap_number),
        Filters.eq("session_key", obj.session_key)
      )

      ReplaceOneModel(filter, obj, ReplaceOptions().upsert(true))
    }
  }

  def insert(data: Seq[Laps]): Future[Unit] = {
    val bulkWrites = updateAndUpsert(data)

    dbConnection.insert(bulkWrites).recover {
      case ex: Throwable =>
        MyLogger.red(s"Failed to insert laps")
    }
  }

  def findByDriverAndSession(params: Map[String, String]): Future[Either[String, Seq[Laps]]] = {
    val order: Document = Document("timestamp" -> 1)
    val driverNumberOpt = params.get("driver_number").flatMap(value => tryParseInt(value))
    val sessionKeyOpt = params.get("session_key").flatMap(value => tryParseInt(value))

    (driverNumberOpt, sessionKeyOpt) match {
      case (Some(driverNumber), Some(sessionKey)) =>
        // Construct the filter with integer values
        val filter = Map("driver_number" -> driverNumber, "session_key" -> sessionKey)

        // Call findAll with converted params
        dbConnection.findAllLaps(filter, order).map { results =>
          if (results.nonEmpty) {
            Right(results)
          } else {
            Left("No results found for the provided driver number and session key.")
          }
        }.recover {
          case ex: Exception =>
            Left(s"An error occurred while fetching data: ${ex.getMessage}")
        }

      case (None, _) =>
        Future.successful(Left("Invalid 'driver_number'. It must be an integer."))

      case (_, None) =>
        Future.successful(Left("Invalid 'session_key'. It must be an integer."))

      case _ =>
        Future.successful(Left("Both 'driver_number' and 'session_key' must be provided as integers."))
    }
  }

  def tryParseInt(value: String): Option[Int] = {
    try {
      Some(value.toInt)
    } catch {
      case _: NumberFormatException => None
    }
  }

}


