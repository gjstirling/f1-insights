package services

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
    val route = "/drivers"
    val paramsWithFilters: Iterable[(String, String)] = Seq(("session_key", "latest"))
    val futureDrivers: Future[Either[String, List[Drivers]]] = f1Api.lookup[List[Drivers]](route, paramsWithFilters)

    futureDrivers.onComplete {
      case Success(Right(drivers)) =>
        repository.insert(drivers).onComplete {
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
}
