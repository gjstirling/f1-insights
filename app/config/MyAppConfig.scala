package config

object MyAppConfig {
  val apiBaseUrl = "https://api.openf1.org/v1"
  val eventsCollection = "events"
  val driverCollection = "drivers"

  val toleranceForLaps = 1.02 // 2% cut off for valid laps
}