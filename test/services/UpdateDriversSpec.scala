package services

import repositories.DriversRepository
import base.UnitSpec


class DriverServiceSpec extends UnitSpec {
  val mockRepository: DriversRepository = mock[DriversRepository]
  val DriverService = new DriverService(mockRepository, mockF1OpenApiController)(ec)

  "update" should {
    "log success when drivers are fetched and inserted successfully" in {

    }

    "log error when API call fails" in {

    }

    "log error when inserting drivers into the repository fails" in {

    }

    "log exception when an exception occurs during API call" in {

    }
  }


}
