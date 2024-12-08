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
  def initialise(): Future[Unit] = {
    val paramsWithFilters: Iterable[(String, String)] = Seq(("year", "2024"), ("session_type", "Qualifying"))

    val eventsFuture: Future[Either[String, List[Event]]] = f1Api.lookup[List[Event]](F1Api.events, paramsWithFilters)

    eventsFuture.flatMap {
      case Right(events) =>
        repository.insertEvents(events).map { _ =>
          MyLogger.blue("Successfully updated events.")
        }.recover {
          case ex =>
            MyLogger.red(s"Error inserting events into repository: ${ex.getMessage}")
            throw ex
        }
      case Left(errors) =>
        MyLogger.red(s"Error fetching events: $errors")
        Future.failed(new Exception(errors))
    }.recover {
      case ex =>
        MyLogger.red(s"Exception occurred while updating events: ${ex.getMessage}")
        Future.failed(ex)
    }
  }


  def getEventList: Future[Seq[Int]] = {
    repository.getSessionKeys(Map.empty)
  }
}