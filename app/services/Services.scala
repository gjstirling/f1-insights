package services

import play.api.mvc.{AnyContent, Request}
import play.api.libs.json._
import upickle.default._

object Services {
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

}
