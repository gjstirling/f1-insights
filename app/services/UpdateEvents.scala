package services

import models.Event
import repositories.EventsRepository
import connectors.F1OpenApi
import upickle.default._

import scala.concurrent.ExecutionContext
import javax.inject._


class UpdateEvents @Inject()(
                                  val repository: EventsRepository,
                                  val f1Api: F1OpenApi
                                )(implicit ec: ExecutionContext) {
  def index: Unit = {
    val route = "/sessions"
    // limiting data to 2024 season and Qualifying only
    val paramsWithFilters: Iterable[(String, String)] = Seq(("year", "2024"), ("session_name", "Qualifying"))

    f1Api.lookup[List[Event]](route, paramsWithFilters).map {
      case Right(race) =>
        implicit val eventRw: ReadWriter[Event] = macroRW
        repository.insert(race)
      case Left(errors) =>
        MyLogger.red("Error with job")
    }

  }
}