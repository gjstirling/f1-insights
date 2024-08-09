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
  def index(): Unit = {
    val route = "/sessions"

    f1Api.lookup[List[Event]](route, Seq.empty).map {
      case Right(event) =>
        implicit val eventRw: ReadWriter[Event] = macroRW
        repository.insert(event)
      case Left(errors) =>
        MyLogger.red("Error with job")
    }

  }
}