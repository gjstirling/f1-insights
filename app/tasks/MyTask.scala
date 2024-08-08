package tasks

import org.apache.pekko.actor.ActorSystem

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import services.{MyLogger, UpdateEvents}

class MyTask @Inject() (actorSystem: ActorSystem, updateEvents: UpdateEvents)(implicit executionContext: ExecutionContext) {

  actorSystem.log.info("[MyTask]: Initializing MyTask...")

  actorSystem.scheduler.scheduleAtFixedRate(
    initialDelay = 1.seconds,
    interval = 5.seconds
  ) { () =>
    MyLogger.blue("[MyTask]: IM DOING MY TASK")
    updateEvents.index
  }

}

