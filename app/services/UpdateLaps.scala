package services

import connectors.F1OpenApi
import models.QualifyingLaps
import repositories.LapsRepository
import services.Services.sortAndFilterLaps

import javax.inject._
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

@Singleton
class UpdateLaps @Inject()(
                            val repository: LapsRepository,
                            val f1Api: F1OpenApi
                                )(implicit ec: ExecutionContext) {
  def index(sessionKeys: Seq[Int]): Unit = {

    val route = "/laps"
    sessionKeys.foreach(key => {
      val params = Map("session_key" -> s"$key")
      Thread.sleep(100)
      val apiCall = f1Api.lookup[List[QualifyingLaps]](route, params)

      apiCall.onComplete {
        case Success(Right(result)) =>
          val filterResult = sortAndFilterLaps(result)

          repository.insert(filterResult)
        case Success(Left(errors)) =>
          MyLogger.red(s"Error fetching laps: $errors")
        case Failure(ex) =>
          MyLogger.red(s"Exception occurred while updating laps: ${ex.getMessage}")
      }

    })

  }

}