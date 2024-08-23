package models

import play.api.libs.json.{Json, OFormat}

case class Event(
                  session_key: Int,
                  session_name: String,
                  date_start: String,
                  date_end: String,
                  gmt_offset: String,
                  session_type: String,
                  meeting_key: Int,
                  location: String,
                  country_key: Int,
                  country_code: String,
                  country_name: String,
                  circuit_key: Int,
                  circuit_short_name: String
                )

case class ShortEvent(
                  session_key: Int,
                  session_name: String,
                  location: String,
                )

object Event {
  implicit val eventFormat: OFormat[Event] = Json.format[Event]

  def convertToShort(event: Event): ShortEvent =
    ShortEvent(
      event.session_key,
      event.session_name,
      event.location
    )
}

object ShortEvent {
  implicit val shortEventFormat: OFormat[ShortEvent] = Json.format[ShortEvent]
}