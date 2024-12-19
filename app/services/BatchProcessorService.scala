package services

import config.F1Api.{BatchSize, promiseDelay}
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.pattern.after
import scala.concurrent.{ExecutionContext, Future}

object BatchProcessorService {

  def processInBatches[T](
                           items: Seq[T]
                         )(process: T => Future[Unit])
                         (implicit actorSystem: ActorSystem, ec: ExecutionContext): Future[Unit] = {
    val batches = items.grouped(BatchSize).toSeq

    def processBatch(batch: Seq[T]): Future[Unit] = {
      val batchFutures = batch.map(process)
      Future.sequence(batchFutures).map(_ => ())
    }

    def delayedFuture(f: => Future[Unit]): Future[Unit] = {
      after(promiseDelay, actorSystem.scheduler)(f)
    }

    batches.foldLeft(Future.successful(())) { (acc, batch) =>
      acc.flatMap(_ => delayedFuture(processBatch(batch)))
    }
  }
}
