package base

import main.scala.models.Event
import play.api.libs.json.{JsValue, Json}

object TestData {

  val sampleEvent: Event = Event(
    session_key = 1,
    session_name = "Qualifying",
    date_start = "2024-05-21T14:00:00Z",
    date_end = "2024-05-21T15:00:00Z",
    gmt_offset = "+00:00",
    session_type = "Qualifying",
    meeting_key = 101,
    location = "Monaco",
    country_key = 33,
    country_code = "MC",
    country_name = "Monaco",
    circuit_key = 44,
    circuit_short_name = "Monaco Circuit"
  )

  val sampleEvents: List[Event] = List(sampleEvent)
  val jsonResponse: JsValue = Json.toJson(sampleEvents)
}