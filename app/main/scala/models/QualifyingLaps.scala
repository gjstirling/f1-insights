package main.scala.models

import play.api.libs.json.{Json, OFormat}

case class QualifyingLaps (
                           date_start: String,
                           driver_number: Int,
                           duration_sector_1: Option[Double],
                           duration_sector_2: Option[Double],
                           duration_sector_3: Option[Double],
                           is_pit_out_lap: Boolean,
                           lap_duration: Option[Double],
                           lap_number: Int,
                           meeting_key: Int,
                           session_key: Int,
                           st_speed: Int
                         )

object QualifyingLaps {
  implicit val eventFormat: OFormat[QualifyingLaps] = Json.format[QualifyingLaps]
}