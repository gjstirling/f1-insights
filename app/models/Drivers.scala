package models

import play.api.libs.json.{Json, OFormat}

case class Drivers(
                    driver_number: Int,
                    broadcast_name: String,
                    full_name: String,
                    team_name: Option[String]
                  )

object Drivers {
  implicit val driverFormat: OFormat[Drivers] = Json.format[Drivers]
}

