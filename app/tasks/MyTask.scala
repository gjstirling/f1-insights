package tasks

import org.apache.pekko.actor.ActorSystem
import javax.inject.Inject
import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._
import services.{MyLogger, UpdateDrivers, UpdateEvents, UpdateLaps}

import java.util.concurrent.TimeUnit

class MyTask @Inject()(actorSystem: ActorSystem, updateEvents: UpdateEvents, updateDrivers: UpdateDrivers, updateLaps: UpdateLaps)(implicit ec: ExecutionContext) {

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

  actorSystem.scheduler.scheduleAtFixedRate(
    initialDelay = 5.seconds,
    interval = 30.seconds
  ) { () =>
    MyLogger.blue("[MyTask][updateLaps]: Running laps job")
    // Collect session keys
    val maxWaitTime: FiniteDuration = Duration(5, TimeUnit.SECONDS)
    val sessionKeys = Await.result(updateEvents.getSessionKeys, maxWaitTime)

    updateLaps.index(sessionKeys)
  }

}


