package services

import connectors.F1OpenApi
import models.Drivers
import repositories.DriversRepository
import upickle.default._

import javax.inject._
import scala.concurrent.ExecutionContext


class UpdateDrivers @Inject()(
                               val repository: DriversRepository,
                               val f1Api: F1OpenApi
                                )(implicit ec: ExecutionContext) {
  def update: Unit = {
    val route = "/drivers"
    val paramsWithFilters: Iterable[(String, String)] = Seq(("session_key", "latest"))

    f1Api.lookup[List[Drivers]](route, paramsWithFilters).map {
      case Right(driver) =>
        implicit val driverRw: ReadWriter[Drivers] = macroRW
        repository.insert(driver)
      case Left(errors) =>
        MyLogger.red("Error with job")
    }

  }
}