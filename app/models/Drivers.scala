package models

import play.api.libs.json.{Json, OFormat}

case class Drivers(
                   driver_number: Int,
                   broadcast_name: String,
                   full_name: String,
                   name_acronym: String,
                   team_name: String,
                   team_colour: String,
                   first_name: String,
                   last_name: String,
                   headshot_url: Option[String],
                   country_code: String,
                   session_key: Int,
                   meeting_key: Int
                 )

case class DriversNameAndNumber(
                                 driver_number: Int,
                    full_name: String
                               )

object Drivers {

  implicit val driverFormat: OFormat[Drivers] = Json.format[Drivers]

  def getNameAndNumber(d: Drivers): DriversNameAndNumber = {
    DriversNameAndNumber(d.driver_number, d.full_name)
  }
}

object DriversNameAndNumber {
  implicit val nameAndNumberWrites: OFormat[DriversNameAndNumber] = Json.format[DriversNameAndNumber]
}

