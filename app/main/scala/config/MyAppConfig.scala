package main.scala.config

import javax.inject.Singleton

@Singleton
class MyAppConfig() {
  val apiBaseUrl = "https://api.openf1.org/v1"

  val database = "f1insights"
  val eventsCollection = "events"

  val hotLapCutOffPercentage = 1.02 // 2% cut off for valid laps
}