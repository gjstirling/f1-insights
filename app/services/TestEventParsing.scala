import play.api.libs.json.Json
import models.Event

object RoundTripTest extends App {
  val json = """
    {
      "session_key": 123,
      "session_name": "Qualifying",
      "date_start": "2024-12-17T22:03:48Z",
      "date_end": "2024-12-18T00:00:00Z",
      "gmt_offset": "+01:00",
      "session_type": "Race",
      "meeting_key": 456,
      "location": "Monaco",
      "country_key": 34,
      "country_code": "MC",
      "country_name": "Monaco",
      "circuit_key": 12,
      "circuit_short_name": "MON"
    }
  """

  // Step 1: Deserialize JSON into Event case class
  val parsedResult = Json.parse(json).validate[Event]

  parsedResult.fold(
    errors => println(s"Deserialization Error: $errors"),
    event => {
      println(s"Parsed Event: $event")

      // Step 2: Serialize Event back to JSON
      val serializedJson = Json.toJson(event).toString()

      // Output serialized JSON
      println(s"Serialized JSON: $serializedJson")
    }
  )
}

