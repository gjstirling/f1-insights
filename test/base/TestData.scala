package base

import models.{Event, LapData, QualifyingLaps}

object TestData {
  val qualifyingLap: QualifyingLaps = QualifyingLaps(
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

  val qualiData: LapData = LapData(
    lap_number = 2,
    sector_1 = 24.699,
    sector_2 = 26.612,
    sector_3 = 25.664,
    lap_time = "1m06.975"
  )

  val mockEventList: Seq[Event] = Seq( Event(
    session_key = 1,
    session_name = "Practice 1",
    date_start = "2024-08-15",
    date_end = "2024-08-15",
    gmt_offset = "+01:00",
    session_type = "Practice",
    meeting_key = 101,
    location = "Silverstone",
    country_key = 44,
    country_code = "GB",
    country_name = "United Kingdom",
    circuit_key = 202,
    circuit_short_name = "SIL"
  ))

  val sampleApiResponse: List[QualifyingLaps] = List(qualifyingLap)
}
