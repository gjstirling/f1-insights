package services

import config.F1Api
import connectors.F1OpenApi
import models.Drivers
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.pattern.after
import repositories.DriversRepository
import upickle.default._

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._


class DriverService @Inject()(
                               val repository: DriversRepository,
                               val f1Api: F1OpenApi,
                               actorSystem: ActorSystem
                             )(implicit ec: ExecutionContext) {

  implicit val driverRw: ReadWriter[Drivers] = macroRW

  private def delayedFuture[T](delay: FiniteDuration)(f: => Future[T]): Future[T] = {
    after(delay, actorSystem.scheduler)(f)
  }

  def addMultiple(eventKeys: Seq[Int], batchSize: Int = 5, delay: FiniteDuration = 1.second): Future[Unit] = {
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
    val futureDrivers: Future[Either[String, List[Drivers]]] = f1Api.lookup[List[Drivers]](F1Api.drivers, paramsWithFilters)

    futureDrivers.flatMap {
      case Right(drivers) =>
        repository.insertDrivers(drivers).map { _ =>
          MyLogger.blue(s"Successfully updated drivers for session_key $eventKey.")
        }.recover { case ex =>
          MyLogger.red(s"Error inserting drivers for session_key $eventKey: ${ex.getMessage}")
        }
      case Left(errors) =>
        Future {
          MyLogger.red(s"Error fetching drivers for session_key $eventKey: $errors")
        }
    }.recover { case ex =>
      MyLogger.red(s"Exception occurred while fetching drivers for session_key $eventKey: ${ex.getMessage}")
    }
  }
}
