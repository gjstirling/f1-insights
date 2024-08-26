package base

import models._

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
    st_speed = None
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

  val mockDriversList: Seq[Drivers] = Seq(
    Drivers(
      driver_number = 44,
      broadcast_name = "L. Hamilton",
      full_name = "Lewis Hamilton",
      name_acronym = "HAM",
      team_name = "Mercedes",
      team_colour = "#00D2BE", // Mercedes team color
      first_name = "Lewis",
      last_name = "Hamilton",
      headshot_url = Some("https://example.com/hamilton.jpg"),
      country_code = "GB",
      session_key = 101,
      meeting_key = 202
    ),
    Drivers(
      driver_number = 33,
      broadcast_name = "M. Verstappen",
      full_name = "Max Verstappen",
      name_acronym = "VER",
      team_name = "Red Bull Racing",
      team_colour = "#1E41FF", // Red Bull Racing team color
      first_name = "Max",
      last_name = "Verstappen",
      headshot_url = Some("https://example.com/verstappen.jpg"),
      country_code = "NL",
      session_key = 101,
      meeting_key = 202
    )
  )


  val sampleApiResponse: List[QualifyingLaps] = List(qualifyingLap)
}
