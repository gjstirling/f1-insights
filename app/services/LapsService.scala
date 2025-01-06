package services

import connectors.F1OpenApi
import models.{LapData, Laps}
import repositories.LapsRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import config.F1Api
import org.apache.pekko.actor.ActorSystem
import config.MyAppConfig._
import play.api.libs.json.{JsObject, Json}
import services.Utilities.toMinutesAndSeconds

@Singleton
class LapsService @Inject()(
                             val repository: LapsRepository,
                             val f1Api: F1OpenApi,
                           )(implicit ec: ExecutionContext, actorSystem: ActorSystem) {

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

  def findByDriverAndSession(params: Map[String, String]): Future[List[LapData]] = {
    repository.findByDriverAndSession(params).flatMap {
      case Right(listOfLaps) if listOfLaps.nonEmpty =>
        val hotLaps = sortAndFilterLaps(listOfLaps.toList)
        if (hotLaps.isEmpty) {
          Future.successful(List.empty)
        } else {
          Future.successful(Laps.toLapData(hotLaps))
        }

      case Right(_) =>
        Future.successful(List.empty)

      case Left(errors) =>
        Future.failed(new Exception(s"Error with request: $errors"))
    }
  }

  def findAverageLaptime(laps: List[LapData]): JsObject = {
    val average = laps.map {
      lap => lap.sector_1 + lap.sector_2 + lap.sector_3
    }.sum / laps.length

    Json.obj(
      "lapData" -> Json.toJson(laps),
      "metadata" -> Json.obj(
        "averageLapTime" -> toMinutesAndSeconds(average),
        "totalHotLaps" -> laps.length
      )
    )
  }

  private def sortAndFilterLaps(laps: List[Laps]): List[Laps] = {
    val sortedLaps = laps.sortBy(_.lap_duration.getOrElse(Double.MaxValue))
    val fastestLapOpt = sortedLaps.headOption.flatMap(_.lap_duration)

    fastestLapOpt match {
      case Some(fastestLap) =>
        val filteredLaps = sortedLaps.filter { lap =>
          lap.lap_duration.exists(_ <= fastestLap * toleranceForLaps
          )
        }
        filteredLaps.sortBy(_.lap_number)

      case None =>
        List.empty[Laps]
    }
  }

}