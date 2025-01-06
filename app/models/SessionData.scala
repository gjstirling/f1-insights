package models

import play.api.libs.json.{Json, OFormat}
import java.time.Instant

case class SessionData(
                        session_key: Int,
                        date: Instant,
                        category: String,
                        flag: Option[String],
                        lap_number: Option[Int],
                        message: String,
                        driver_number: Option[Int]
                      )

object SessionData {
  implicit val format: OFormat[SessionData] = Json.format[SessionData]
}
