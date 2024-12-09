package services

import config.MyAppConfig.toleranceForLaps
import models.Laps
import play.api.mvc.{AnyContent, Request}
import play.api.libs.json._
import upickle.default._

object Utilities {
  def extractParams(request: Request[AnyContent]): Iterable[(String, String)] = {
    request.queryString.flatMap {
      case (key, values) =>
        values.headOption.map(value => (key, value))
    }
  }

  def convertToJsonArray[T](list: List[T])(implicit rw: ReadWriter[T]): JsArray = {
    val jsonList: List[JsValue] = list.map(event => Json.parse(write(event)))
    JsArray(jsonList)
  }

  def toMinutesAndSeconds(lapTime: Double ): String = {
    val minutes = if (lapTime > 60.00) 1
    val seconds = ((lapTime - 60) * 1000).round / 1000.toDouble
    s"${minutes}m$seconds"
  }

  def sortAndFilterLaps(laps: List[Laps]): List[Laps] = {
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
