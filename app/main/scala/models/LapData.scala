package main.scala.models

import play.api.libs.json.{JsValue, Json, OFormat, Reads, Writes}

case class LapData(
                    lap_number: Int,
                    sector_1: Option[Double],
                    sector_2: Option[Double],
                    sector_3: Option[Double],
                    lap_time: Option[Double]
                  )

object LapData {
  implicit val format: OFormat[LapData] = Json.format[LapData]
}