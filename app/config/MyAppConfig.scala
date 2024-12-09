package config

object MyAppConfig {
  val toleranceForLaps = 1.10
  val username: String = scala.sys.env("DB_USERNAME")
  val password: String = scala.sys.env("DB_PASSWORD")
  val database: String = scala.sys.env("DATABASE")
  val connectionString = s"mongodb+srv://$username:$password@cluster0.zobrk9b.mongodb.net/"

  val BatchSize = 5
  val promiseDelay = 1
}

object F1Api {
  val BaseUrl = "https://api.openf1.org/v1"
  val drivers = "/drivers"
  val laps = "/laps"
  val events = "/sessions"
}