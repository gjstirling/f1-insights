package services

import models.Event
import repositories.EventsRepository
import connectors.F1OpenApi
import scala.concurrent.{ExecutionContext, Future}
import javax.inject._
import config.F1Api

class EventsService @Inject()(
                               val repository: EventsRepository,
                               val f1Api: F1OpenApi
                             )(implicit ec: ExecutionContext) {
  def initialise(): Future[Seq[Int]] = {
    val paramsWithFilters: Iterable[(String, String)] = Seq(("year", "2024"), ("session_type", "Qualifying"))

    val eventsFuture: Future[Either[String, List[Event]]] = f1Api.lookup[List[Event]](F1Api.events, paramsWithFilters)

    eventsFuture.flatMap {
      case Right(events) =>
        repository.insertEvents(events).flatMap { _ =>
          MyLogger.blue("Successfully updated events.")
          repository.getSessionKeys(Map.empty) // Fetch session keys after inserting events
        }.recover {
          case ex =>
            MyLogger.red(s"Error inserting events into repository: ${ex.getMessage}")
            throw ex
        }
      case Left(errors) =>
        MyLogger.red(s"Error fetching events: $errors")
        Future.failed(new Exception(errors))
    }.recoverWith {
      case ex =>
        MyLogger.red(s"Exception occurred while updating events: ${ex.getMessage}")
        Future.failed(ex)
    }
  }
}