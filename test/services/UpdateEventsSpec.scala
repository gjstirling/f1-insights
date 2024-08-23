package services

import repositories.EventsRepository
import base.UnitSpec


class UpdateEventsSpec extends UnitSpec {
  val mockRepository: EventsRepository = mock[EventsRepository]
  val updateDrivers = new UpdateEvents(mockRepository, mockF1OpenApiController)(ec)

  "update" should {
    "log success when events are fetched and inserted successfully" in {

    }

    "log error when API call fails" in {

    }

    "log error when inserting events into the repository fails" in {

    }

    "log exception when an exception occurs during API call" in {

    }
  }


}
