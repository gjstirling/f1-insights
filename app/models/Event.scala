package models

import play.api.libs.json.{Json, OFormat}

case class Event(
                  location: String,
                  country_key: Int,
                  country_code: String,
                  country_name: String,
                  circuit_key: Int,
                  circuit_short_name: String,
                  session_type: String,
                  session_name: String,
                  date_start: String,
                  date_end: String,
                  gmt_offset: String,
                  session_key: Int,
                  meeting_key: Int,
                  year: Int
                )

object Event {
  implicit val eventFormat: OFormat[Event] = Json.format[Event]
}

