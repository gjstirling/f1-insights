package config

object MyAppConfig {
  val apiBaseUrl = "https://api.openf1.org/v1"
  val toleranceForLaps = 1.02 // 2% cut off for valid laps

  val username: String = scala.sys.env("DB_USERNAME")
  val password: String = scala.sys.env("DB_PASSWORD")
  val database: String = scala.sys.env("DATABASE")
  val connectionString = s"mongodb+srv://$username:$password@cluster0.zobrk9b.mongodb.net/"
}