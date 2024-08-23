package tasks

import org.apache.pekko.actor.ActorSystem
import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import services.{MyLogger, UpdateDrivers, UpdateEvents}

class MyTask @Inject() (actorSystem: ActorSystem, updateEvents: UpdateEvents, updateDrivers: UpdateDrivers)(implicit ec: ExecutionContext) {

  actorSystem.scheduler.scheduleAtFixedRate(
    initialDelay = 1.seconds,
    interval = 240.seconds
  ) { () =>
    MyLogger.blue("[MyTask][updateEvents]: running events job")
    updateEvents.index()
  }

  actorSystem.scheduler.scheduleAtFixedRate(
    initialDelay = 1.seconds,
    interval = 1.days
  ) { () =>
    MyLogger.blue("[MyTask][updateDrivers]: Running drivers job")
    updateDrivers.update()
  }

}


