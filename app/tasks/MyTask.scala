package tasks

import org.apache.pekko.actor.ActorSystem
import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import services.{MyLogger, DriverService, EventsService, LapsService}

class MyTask @Inject()(actorSystem: ActorSystem, EventsService: EventsService, DriverService: DriverService, LapsService: LapsService)(implicit ec: ExecutionContext) {

  actorSystem.scheduler.scheduleOnce(1.second) {
    MyLogger.blue("[MyTask][DriverService]: Initialising events collection")
    EventsService.index()
  }

  actorSystem.scheduler.scheduleOnce(1.minute) {
    MyLogger.blue("[MyTask][Scheduler]: ATTEMPTING TASK")


    val initializationFuture = for {
      events <- EventsService.getEventList
      _ = {
        MyLogger.red("[MyTask][updateLaps]: Adding Drivers with events...")
        MyLogger.red("[MyTask][updateLaps]: EVENT LIST: " + events)
      }
      _ <- DriverService.init(events).map { _ =>
        MyLogger.blue("[MyTask][updateDrivers]: Driver collection initialized")
      }
//      _ <- {
//        MyLogger.red("[MyTask][updateLaps]: Adding laps for events...")
//        LapsService.init(events).map { _ =>
//          MyLogger.blue("[MyTask][updateLaps]: Lap times collection initialized")
//        }
//      }
    } yield ()

    initializationFuture.recover {
      case ex: Throwable =>
        MyLogger.red(s"Failed to initialize collections: $ex")
    }
  }

  actorSystem.scheduler.scheduleAtFixedRate(
    initialDelay = 1.hour,
    interval = 1.days
  ) { () =>
    MyLogger.blue("[MyTask][EventsService]: Running events job (checking for new events)")
    EventsService.index()
  }

}

