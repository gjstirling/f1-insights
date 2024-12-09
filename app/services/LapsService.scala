package services

import connectors.F1OpenApi
import models.Laps
import repositories.LapsRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import config.F1Api
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.pattern.after
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import config.MyAppConfig._

@Singleton
class LapsService @Inject()(
                             val repository: LapsRepository,
                             val f1Api: F1OpenApi,
                             actorSystem: ActorSystem
                           )(implicit ec: ExecutionContext) {

  private def delayedFuture[T](delay: FiniteDuration)(f: => Future[T]): Future[T] = {
    after(delay, actorSystem.scheduler)(f)
  }

  def addMultiple(eventKeys: Seq[Int])(implicit batchSize: Int = BatchSize, delay: FiniteDuration = promiseDelay.second): Future[Unit] = {
    val batches = eventKeys.grouped(batchSize).toSeq

    def processBatch(batch: Seq[Int]): Future[Unit] = {
      val batchFutures = batch.map(add)
      Future.sequence(batchFutures).map(_ => ())
    }

    batches.foldLeft(Future.successful(())) { (acc, batch) =>
      acc.flatMap(_ => delayedFuture(delay)(processBatch(batch)))
    }
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