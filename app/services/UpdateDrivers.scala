package services

import config.F1Api
import connectors.F1OpenApi
import models.Drivers
import repositories.DriversRepository
import upickle.default._

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class UpdateDrivers @Inject()(
                               val repository: DriversRepository,
                               val f1Api: F1OpenApi
                             )(implicit ec: ExecutionContext) {

  implicit val driverRw: ReadWriter[Drivers] = macroRW

  def update(): Unit = {
    val paramsWithFilters: Iterable[(String, String)] = Seq(("session_key", "latest"))
    val futureDrivers: Future[Either[String, List[Drivers]]] = f1Api.lookup[List[Drivers]](F1Api.drivers, paramsWithFilters)

    futureDrivers.onComplete {
      case Success(Right(drivers)) =>
        repository.insertDrivers(drivers).onComplete {
          case Success(_) =>
            MyLogger.blue("Successfully updated drivers.")
          case Failure(ex) =>
            MyLogger.red(s"Error inserting drivers into repository: ${ex.getMessage}")
        }
      case Success(Left(errors)) =>
        MyLogger.red(s"Error fetching drivers: $errors")
      case Failure(ex) =>
        MyLogger.red(s"Exception occurred while updating drivers: ${ex.getMessage}")
    }
  }

  def init(eventKeyList: Seq[Int]): Unit = {

    val futureUpdates: Seq[Future[Unit]] = eventKeyList.map { eventKeyValue =>
      val paramsWithFilters: Iterable[(String, String)] = Seq(("session_key", eventKeyValue.toString))
      val futureDrivers: Future[Either[String, List[Drivers]]] = f1Api.lookup[List[Drivers]](F1Api.drivers, paramsWithFilters)

      futureDrivers.flatMap {
        case Right(drivers) =>
          repository.insertDrivers(drivers).map { _ =>
            MyLogger.blue(s"Successfully updated drivers for session_key $eventKeyValue.")
          }.recover { case ex =>
            MyLogger.red(s"Error inserting drivers for session_key $eventKeyValue: ${ex.getMessage}")
          }
        case Left(errors) =>
          Future {
            MyLogger.red(s"Error fetching drivers for session_key $eventKeyValue: $errors")
          }
      }.recover { case ex =>
        MyLogger.red(s"Exception occurred while fetching drivers for session_key $eventKeyValue: ${ex.getMessage}")
      }
    }

    Future.sequence(futureUpdates).onComplete {
      case Success(_) =>
        MyLogger.blue("All driver updates completed.")
      case Failure(ex) =>
        MyLogger.red(s"Some driver updates failed: ${ex.getMessage}")
    }
  }

}
