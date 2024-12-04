package tasks

import org.apache.pekko.actor.ActorSystem
import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import services.{MyLogger, DriverService, EventsService, LapsService}

class MyTask @Inject()(actorSystem: ActorSystem, EventsService: EventsService, DriverService: DriverService, updateLaps: LapsService)(implicit ec: ExecutionContext) {

  actorSystem.scheduler.scheduleOnce(1.second) {
    MyLogger.blue("[MyTask][DriverService]: Initialising events collection")
    EventsService.index()
  }

  actorSystem.scheduler.scheduleOnce(5.seconds) {
    val eventsFuture = EventsService.getEventList()

    eventsFuture.map { events =>
      MyLogger.blue("[MyTask][updateLaps]: Initialising lap times collection")
      DriverService.init(events)
      updateLaps.initilize(events)
    }.recover {
      case ex: Throwable =>
        MyLogger.red(s"Failed to initialize lap times collection: $ex")
    }
  }

  actorSystem.scheduler.scheduleAtFixedRate(
    initialDelay = 1.hour,
    interval = 1.days
  ) { () =>
    MyLogger.blue("[MyTask][EventsService]: Running events job (checking for new events)")
    EventsService.index()
  }

  actorSystem.scheduler.scheduleAtFixedRate(
    initialDelay = 2.hour,
    interval = 1.days
  ) { () =>
    MyLogger.blue("[MyTask][update]: Running driver and laps job (checking for new drivers and lap times)")
    val eventsFuture = EventsService.getEventList()

    eventsFuture.map { events =>
      DriverService.init(events)
      updateLaps.initilize(events)
    }.recover {
      case ex: Throwable =>
        MyLogger.red(s"Failed to initialize lap times collection: $ex")
    }
  }

}


