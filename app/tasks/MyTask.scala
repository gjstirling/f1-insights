package tasks

import org.apache.pekko.actor.ActorSystem
import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import services.{MyLogger, UpdateDrivers, UpdateEvents, UpdateLaps}

class MyTask @Inject() (actorSystem: ActorSystem, updateEvents: UpdateEvents, updateDrivers: UpdateDrivers, updateLaps: UpdateLaps)(implicit ec: ExecutionContext) {

  actorSystem.scheduler.scheduleOnce(1.second) {
    MyLogger.blue("[MyTask][updateDrivers]: Initialising events collection")
    updateEvents.index()
  }

  actorSystem.scheduler.scheduleOnce(5.seconds) {
    val eventsFuture = updateEvents.getEventList()

    eventsFuture.map { events =>
      MyLogger.blue("[MyTask][updateLaps]: Initialising lap times collection")
      updateDrivers.init(events)
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
    MyLogger.blue("[MyTask][updateEvents]: Running events job (checking for new events)")
    updateEvents.index()
  }

  actorSystem.scheduler.scheduleAtFixedRate(
    initialDelay = 2.hour,
    interval = 1.days
  ) { () =>
    MyLogger.blue("[MyTask][update]: Running driver and laps job (checking for new drivers and lap times)")
    val eventsFuture = updateEvents.getEventList()

    eventsFuture.map { events =>
      updateDrivers.init(events)
      updateLaps.initilize(events)
    }.recover {
      case ex: Throwable =>
        MyLogger.red(s"Failed to initialize lap times collection: $ex")
    }
  }

}


