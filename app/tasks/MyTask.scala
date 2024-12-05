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
    val eventsFuture = EventsService.getEventList

    eventsFuture.flatMap { events =>
      MyLogger.blue("[MyTask][updateLaps]: Initializing lap times collection")
      val driverServiceFuture = DriverService.init(events)
      val updateLapsFuture = LapsService.initilize(events)

      for {
        _ <- driverServiceFuture
        _ <- updateLapsFuture
      } yield {
        MyLogger.blue("[MyTask][updateLaps]: Lap times collection initialized successfully.")
      }
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

}

