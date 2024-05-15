package main.scala.models

import play.api.libs.json.{Json, OFormat}


case class Event(
                  location: String,
                  session_name: String,
                  date_start: String,
                  gmt_offset: String,
                  session_key: Int,
                  meeting_key: Int,
                  year: Int
                )

object Event {
  implicit val eventFormat: OFormat[Event] = Json.format[Event]
}

