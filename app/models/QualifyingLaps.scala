package models

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
                           st_speed: Option[Int]
                         )

object QualifyingLaps {
  implicit val eventFormat: OFormat[QualifyingLaps] = Json.format[QualifyingLaps]

  def toLapData(qualiLaps: List[QualifyingLaps]): List[LapData] = {
    qualiLaps.map { lap =>
      val minutes = if (lap.lap_duration.get > 60.00) 1
      val seconds = ((lap.lap_duration.get - 60) * 1000).round / 1000.toDouble
      val totalLapTime = s"${minutes}m$seconds"

      LapData(
        lap_number = lap.lap_number,
        lap.duration_sector_1.getOrElse(0),
        lap.duration_sector_2.getOrElse(0),
        lap.duration_sector_3.getOrElse(0),
        totalLapTime
      )
    }
  }
}

case class LapData(
                    lap_number: Int,
                    sector_1: Double,
                    sector_2: Double,
                    sector_3: Double,
                    lap_time: String
                  )

object LapData {
  implicit val format: OFormat[LapData] = Json.format[LapData]
}