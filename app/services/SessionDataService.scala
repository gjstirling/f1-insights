package services

import connectors.F1OpenApi
import repositories.SessionDataRepository
import config.F1Api
import models.SessionData
import org.apache.pekko.actor.ActorSystem

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

class SessionDataService @Inject()(
                               val repository: SessionDataRepository,
                               val f1Api: F1OpenApi
                             )(implicit ec: ExecutionContext, actorSystem: ActorSystem) {

  def addMultiple(eventKeys: Seq[Int]): Future[Unit] = {
    BatchProcessorService.processInBatches(eventKeys)(add)(actorSystem, ec)
  }

  def add(eventKey: Int): Future[Unit] = {
    val paramsWithFilters: Iterable[(String, String)] = Seq(("session_key", eventKey.toString))
    val futureDrivers: Future[Either[String, List[SessionData]]] = f1Api.lookup[List[SessionData]](F1Api.sessionData, paramsWithFilters)

    futureDrivers.flatMap {
      case Right(data) =>
        repository.insertData(data).map { _ =>
          MyLogger.blue(s"Successfully added session data for event: $eventKey.")
        }.recover { case ex =>
          MyLogger.red(s"Error inserting session data for session_key $eventKey: ${ex.getMessage}")
        }
      case Left(errors) =>
        Future {
          MyLogger.red(s"Error fetching session data for session_key $eventKey: $errors")
        }
    }.recover { case ex =>
      MyLogger.red(s"Exception occurred while fetching session data for session_key $eventKey: ${ex.getMessage}")
    }
  }

  def find(): Future[Seq[SessionData]] = {
    repository.getBySessionKey(9658)
  }


}