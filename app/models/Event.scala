package models

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

case class Event(
                  location: String,
                  country_key: Int,
                  country_code: String,
                  country_name: String,
                  circuit_key: Int,
                  circuit_short_name: String,
                  session_type: String,
                  session_name: String,
                  date_start: String,
                  date_end: String,
                  gmt_offset: String,
                  session_key: Int,
                  meeting_key: Int,
                  year: Int
                )

object Event {
  implicit val reads: Reads[Event] = (
    (JsPath \ "location").read[String] and
      (JsPath \ "country_key").read[Int] and
      (JsPath \ "country_code").read[String] and
      (JsPath \ "country_name").read[String] and
      (JsPath \ "circuit_key").read[Int] and
      (JsPath \ "circuit_short_name").read[String] and
      (JsPath \ "session_type").read[String] and
      (JsPath \ "session_name").read[String] and
      (JsPath \ "date_start").read[String] and
      (JsPath \ "date_end").read[String] and
      (JsPath \ "gmt_offset").read[String] and
      (JsPath \ "session_key").read[Int] and
      (JsPath \ "meeting_key").read[Int] and
      (JsPath \ "year").read[Int]
    )(Event.apply _)
}
