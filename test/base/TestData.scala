package base

import main.scala.models.{Event, LapData, QualifyingLaps}
import play.api.libs.json.{JsValue, Json, Reads}

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

  val qualifyingLap = QualifyingLaps(
    date_start = "2024-05-18T14:04:14.304000+00:00",
    driver_number = 55,
    duration_sector_1 = Some(24.699),
    duration_sector_2 = Some(26.612),
    duration_sector_3 = Some(25.664),
    is_pit_out_lap = false,
    lap_duration = Some(76.975),
    lap_number = 2,
    meeting_key = 1235,
    session_key = 9511,
    st_speed = 292
  )

  val qualiData = LapData(
    lap_number = 2,
    sector_1 = Some(24.699),
    sector_2 = Some(26.612),
    sector_3 = Some(25.664),
    lap_time = Some(76.975)
  )

  val sampleEvents: List[Event] = List(sampleEvent)
  val jsonResponse: JsValue = Json.toJson(sampleEvents)

  val sampleApiResponse: List[QualifyingLaps] = List(qualifyingLap)
}
