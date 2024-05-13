package controllers

import geny.Bytes
import play.api.libs.json.{JsError, JsPath, JsSuccess, JsValue, Json, Reads}
import play.api.libs.functional.syntax._

import javax.inject._
import play.api.mvc._
import requests.Response

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  case class Race(
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

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    implicit val raceReads: Reads[Race] = (
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
      )(Race.apply _)

    val response: Response = requests.get("https://api.openf1.org/v1/sessions")
    val data: Bytes = response.data
    val json: JsValue = Json.parse(data.array)

    val raceResult = json.validate[List[Race]]

    raceResult match {
      case JsSuccess(race, _) =>
        race.foreach(event => {
          println(s"Parsed race: ${event}\n")
        })
      case JsError(errors) =>
        // Failed to parse JSON into a Race object
        println(s"Failed to parse race: $errors")
    }

    Ok("Hello World")
  }
}