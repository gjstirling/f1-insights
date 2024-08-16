package services

import connectors.F1OpenApi
import models.Laps
import repositories.LapsRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


class UpdateLaps @Inject()(
                                  val repository: LapsRepository,
                                  val f1Api: F1OpenApi
                                )(implicit ec: ExecutionContext) {
  def index(): Unit = {
    val route = "/laps"
    val params = Map("session_key" -> "9570")
    val lapsFuture: Future[Either[String, List[Laps]]] = f1Api.lookup[List[Laps]](route, params)

    lapsFuture.onComplete {
      case Success(Right(laps)) =>
        repository.insertLaps(laps).onComplete {
          case Success(_) =>
            MyLogger.blue("Successfully updated laps.")
          case Failure(ex) =>
            MyLogger.red(s"Error inserting laps into repository: ${ex.getMessage}")
        }
      case Success(Left(errors)) =>
        MyLogger.red(s"Error fetching laps: $errors")
      case Failure(ex) =>
        MyLogger.red(s"Exception occurred while updating laps: ${ex.getMessage}")
    }
  }
}