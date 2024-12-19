package models

import play.api.libs.json.{Json, OFormat}
import services.Utilities.toMinutesAndSeconds
import java.time.Instant

case class Laps (
                           date_start: Instant,
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

object Laps {
  implicit val eventFormat: OFormat[Laps] = Json.format[Laps]

  def toLapData(qualiLaps: List[Laps]): List[LapData] = {
    qualiLaps.flatMap { lap =>
      lap.lap_duration match {
        case Some(duration) if duration > 0 =>
          Some(LapData(
            lap_number = lap.lap_number,
            sector_1 = lap.duration_sector_1.getOrElse(0),
            sector_2 = lap.duration_sector_2.getOrElse(0),
            sector_3 = lap.duration_sector_3.getOrElse(0),
            lap_time = toMinutesAndSeconds(duration)
          ))

        case None | Some(0) =>
          throw new IllegalArgumentException("Invalid lap_duration: Must not be zero or None")
      }
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