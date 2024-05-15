package main.scala.models

import play.api.libs.json.{Json, OFormat}


case class Event(
                  location: String,
                  session_name: String,
                  date_start: String,
                  gmt_offset: String,
                  session_key: Int,
                  meeting_key: Int,
                  year: Int
                )

object Event {
  implicit val eventFormat: OFormat[Event] = Json.format[Event]

//  implicit val eventReader: BSONDocumentReader[Event] =
//    BSONDocumentReader.from[Event] { bson =>
//      for {
//        location <- bson.getAsTry[String]("location")
//        session_name <- bson.getAsTry[String]("session_name")
//        date_start <- bson.getAsTry[String]("date_start")
//        gmt_offset <- bson.getAsTry[String]("gmt_offset")
//        session_key <- bson.getAsTry[Int]("session_key")
//        meeting_key <- bson.getAsTry[Int]("meeting_key")
//        year <- bson.getAsTry[Int]("year")
//      } yield Event(location, session_name, date_start, gmt_offset, session_key, meeting_key, year)
//    }
//
//  implicit val eventWriter: BSONDocumentWriter[Event] =
//    BSONDocumentWriter[Event] { event =>
//      BSONDocument(
//        "location" -> event.location,
//        "session_name" -> event.session_name,
//        "date_start" -> event.date_start,
//        "gmt_offset" -> event.gmt_offset,
//        "session_key" -> event.session_key,
//        "meeting_key" -> event.meeting_key,
//        "year" -> event.year
//      )
//    }
}

