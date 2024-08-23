package services

import models.Event
import repositories.EventsRepository
import connectors.F1OpenApi
import upickle.default._

import scala.concurrent.{ExecutionContext, Future}
import javax.inject._
import scala.util.{Failure, Success}


class UpdateEvents @Inject()(
                                  val repository: EventsRepository,
                                  val f1Api: F1OpenApi
                                )(implicit ec: ExecutionContext) {
  def index(): Unit = {
    val route = "/sessions"
    val eventsFuture: Future[Either[String, List[Event]]] = f1Api.lookup[List[Event]](route, Seq.empty)

    eventsFuture.onComplete {
      case Success(Right(events)) =>
        repository.insertEvents(events).onComplete {
          case Success(_) =>
            MyLogger.blue("Successfully updated events.")
          case Failure(ex) =>
            MyLogger.red(s"Error inserting events into repository: ${ex.getMessage}")
        }
      case Success(Left(errors)) =>
        MyLogger.red(s"Error fetching events: $errors")
      case Failure(ex) =>
        MyLogger.red(s"Exception occurred while updating events: ${ex.getMessage}")
    }

  }
}