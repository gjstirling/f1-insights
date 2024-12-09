package services

import connectors.F1OpenApi
import models.Laps
import repositories.LapsRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import config.F1Api
import org.apache.pekko.actor.ActorSystem
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import config.MyAppConfig._

@Singleton
class LapsService @Inject()(
                             val repository: LapsRepository,
                             val f1Api: F1OpenApi,
                           )(implicit ec: ExecutionContext, actorSystem: ActorSystem)
{

  def addMultiple(eventKeys: Seq[Int]): Future[Unit] = {
    BatchProcessorService.processInBatches(eventKeys)(add)(actorSystem, ec)
  }

  def add(eventKey: Int): Future[Unit] = {
    val paramsWithFilters: Iterable[(String, String)] = Seq(("session_key", eventKey.toString))
    val futureDrivers: Future[Either[String, List[Laps]]] = f1Api.lookup[List[Laps]](F1Api.laps, paramsWithFilters)

    futureDrivers.flatMap {
      case Right(laps) =>
        repository.insert(laps).map { _ =>
          MyLogger.blue(s"Successfully updated laps for session_key $eventKey.")
        }.recover { case ex =>
          MyLogger.red(s"Error inserting laps for session_key $eventKey: ${ex.getMessage}")
        }
      case Left(errors) =>
        Future {
          MyLogger.red(s"Error fetching laps for session_key $eventKey: $errors")
        }
    }.recover { case ex =>
      MyLogger.red(s"Exception occurred while fetching laps for session_key $eventKey: ${ex.getMessage}")
    }
  }


}