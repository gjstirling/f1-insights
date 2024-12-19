package services

import config.F1Api
import connectors.F1OpenApi
import models.Drivers
import repositories.DriversRepository
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import org.apache.pekko.actor.ActorSystem
import play.api.libs.json.{Json, OFormat}


class DriverService @Inject()(
                               val repository: DriversRepository,
                               val f1Api: F1OpenApi
                             )(implicit ec: ExecutionContext, actorSystem: ActorSystem) {

  implicit val driverFormat: OFormat[Drivers] = Json.format[Drivers]

  def addMultiple(eventKeys: Seq[Int]): Future[Unit] = {
    BatchProcessorService.processInBatches(eventKeys)(add)(actorSystem, ec)
  }

  def add(eventKey: Int): Future[Unit] = {
    val paramsWithFilters: Iterable[(String, String)] = Seq(("session_key", eventKey.toString))
    val futureDrivers: Future[Either[String, List[Drivers]]] = f1Api.lookup[List[Drivers]](F1Api.drivers, paramsWithFilters)

    futureDrivers.flatMap {
      case Right(drivers) =>
        repository.insertDrivers(drivers).map { _ =>
          MyLogger.blue(s"Successfully updated drivers for session_key $eventKey.")
        }.recover { case ex =>
          MyLogger.red(s"Error inserting drivers for session_key $eventKey: ${ex.getMessage}")
        }
      case Left(errors) =>
        Future {
          MyLogger.red(s"Error fetching drivers for session_key $eventKey: $errors")
        }
    }.recover { case ex =>
      MyLogger.red(s"Exception occurred while fetching drivers for session_key $eventKey: ${ex.getMessage}")
    }
  }
}
