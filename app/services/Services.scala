package services

import play.api.mvc.{AnyContent, Request}
import play.api.libs.json._
import upickle.default._

object Services {

  object MyLogger {
    def info(str: String): Unit = {
      println(Console.MAGENTA + str + Console.RESET)
    }

    def red(str: String): Unit = {
      println(Console.RED + str + Console.RESET)
    }

    def blue(str: String): Unit = {
      println(Console.BLUE + str + Console.RESET)
    }
  }

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

}
