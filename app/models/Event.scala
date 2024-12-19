package models

import play.api.libs.json.{Json, OFormat}
import java.time.Instant

case class Event(
                  session_key: Int,
                  session_name: String,
                  date_start: Instant,
                  date_end: Instant,
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
                  country_name: String,
                     )

object Event {
  implicit val eventFormat: OFormat[Event] = Json.format[Event]

  def convertToShort(event: Event): ShortEvent =
    ShortEvent(
      event.session_key,
      event.session_name,
      event.location,
      event.country_name
    )

}

object ShortEvent {
  implicit val shortEventFormat: OFormat[ShortEvent] = Json.format[ShortEvent]
}