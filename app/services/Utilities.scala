package services

import play.api.mvc.{AnyContent, Request}

object Utilities {
  def extractParams(request: Request[AnyContent]): Iterable[(String, String)] = {
    request.queryString.flatMap {
      case (key, values) =>
        values.headOption.map(value => (key, value))
    }
  }

  def toMinutesAndSeconds(lapTime: Double ): String = {
    val minutes = if (lapTime > 60.00) 1
    val seconds = ((lapTime - 60) * 1000).round / 1000.toDouble
    s"${minutes}m$seconds"
  }
}

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
