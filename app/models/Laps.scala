package models

import play.api.libs.json.{Json, OFormat}

case class Laps (
                           date_start: String,
                           driver_number: Int,
                           duration_sector_1: Option[Double],
                           duration_sector_2: Double,
                           duration_sector_3: Option[Double],
                           i1_speed: Int,
                           i2_speed: Int,
                           is_pit_out_lap: Boolean,
                           lap_duration: Option[Double],
                           lap_number: Int,
                           meeting_key: Int,
                           session_key: Int,
                           st_speed: Int
                         )

object Laps {
  implicit val sessionLapsFormat: OFormat[Laps] = Json.format[Laps]
}
