package services

import connectors.F1OpenApi
import models.Laps
import repositories.LapsRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import config.F1Api

@Singleton
class LapsService @Inject()(
                             val repository: LapsRepository,
                             val f1Api: F1OpenApi
                           )(implicit ec: ExecutionContext) {

  def initilize(events: Seq[Int]): Future[Unit] = {
    val futureLaps: Seq[Future[Unit]] = events.map { key =>
      val params = Map("session_key" -> s"$key")
      // Sleep for 200ms to stay within rate limits
      Thread.sleep(200)
      val apiCall = f1Api.lookup[List[Laps]](F1Api.laps, params)

      apiCall.flatMap {
        case Right(result) =>
          repository.insert(result).map { _ =>
            MyLogger.blue(s"Successfully updated laps for session_key $key.")
          }
        case Left(errors) =>
          Future {
            MyLogger.red(s"Error fetching laps for session_key $key: $errors")
          }
      }.recover {
        case ex =>
          MyLogger.red(s"Exception occurred while updating laps for session_key $key: ${ex.getMessage}")
      }
    }

    Future.sequence(futureLaps).map(_ => ())
  }
}