package tasks

import javax.inject.Inject
import scala.concurrent.Await
import services._

import scala.concurrent.duration._

class MyTask @Inject()( EventsService: EventsService, DriverService: DriverService, LapsService: LapsService, SessionDataService: SessionDataService) {

  private def scheduleTask(): Unit = {
    MyLogger.info("[MyTask] -- Initialising MongoDB Data")
    MyLogger.blue("[MyTask][EVENTS]: Initialising events collection")
    val events = Await.result(EventsService.initialise(), 2.minutes)
    addDrivers(events)
  }

  private def addDrivers(events: Seq[Int]): Unit = {
    Await.result(DriverService.addMultiple(events), 2.minutes)

    addLaps(events)
  }

  private def addLaps(events: Seq[Int]): Unit = {
    Await.result(LapsService.addMultiple(events), 4.minutes)

    addSessionData(events)
  }

  private def addSessionData(events: Seq[Int]): Unit = {
    Await.result(SessionDataService.addMultiple(events), 4.minutes)
  }

  scheduleTask()
}
