package services

import connectors.F1OpenApi
import models.Laps
import repositories.LapsRepository
import javax.inject._
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import config.F1Api

@Singleton
class UpdateLaps @Inject()(
                            val repository: LapsRepository,
                            val f1Api: F1OpenApi
                          )(implicit ec: ExecutionContext) {

  def initilize(events: Seq[Int]): Unit = {
    events.foreach(key => {
      val params = Map("session_key" -> s"$key")
      Thread.sleep(200) // Stay within call limit
      val apiCall = f1Api.lookup[List[Laps]](F1Api.laps, params)

      apiCall.onComplete {
        case Success(Right(result)) =>
          repository.insert(result)
        case Success(Left(errors)) =>
          MyLogger.red(s"Error fetching laps: $errors")
        case Failure(ex) =>
          MyLogger.red(s"Exception occurred while updating laps: ${ex.getMessage}")
      }
    })

  }

}