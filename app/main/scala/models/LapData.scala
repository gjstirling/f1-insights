package main.scala.models

import play.api.libs.json.{Json, OFormat}

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