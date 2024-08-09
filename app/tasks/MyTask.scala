package tasks

import org.apache.pekko.actor.ActorSystem

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import services.{MyLogger, UpdateDrivers, UpdateEvents}

class MyTask @Inject() (actorSystem: ActorSystem, updateEvents: UpdateEvents, updateDrivers: UpdateDrivers)(implicit ec: ExecutionContext) {

  actorSystem.log.info("[MyTask]: Initializing MyTasks...")

  actorSystem.scheduler.scheduleAtFixedRate(
    initialDelay = 1.seconds,
    interval = 10.seconds
  ) { () =>
    MyLogger.blue("[MyTask][updateEvents]: running events job")
    updateEvents.index

    MyLogger.blue("[MyTask][updateDrivers]: running drivers job")
    updateDrivers.update
  }

}


