package config

object MyAppConfig {
  val apiBaseUrl = "https://api.openf1.org/v1"
  val database = "f1insights"
  val eventsCollection = "events"

  val toleranceForLaps = 1.02 // 2% cut off for valid laps
}