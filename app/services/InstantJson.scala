package services

import play.api.libs.json._
import java.time.Instant
import java.time.format.DateTimeParseException

object InstantJson {
  implicit val instantReads: Reads[Instant] = Reads[Instant] { json =>
    json.validate[String].flatMap { str =>
      try {
        JsSuccess(Instant.parse(str))
      } catch {
        case _: DateTimeParseException => JsError("Invalid date format")
      }
    }
  }

  implicit val instantWrites: Writes[Instant] = Writes[Instant] { instant =>
    JsString(instant.toString)
  }

  implicit val instantFormat: Format[Instant] = Format(instantReads, instantWrites)
}
