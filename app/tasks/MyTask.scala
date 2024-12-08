package tasks

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.util.Timeout
import javax.inject.Inject
import scala.concurrent.ExecutionContext
import services.{DriverService, EventsService, MyLogger}

import scala.concurrent.duration._

class MyTask @Inject()(actorSystem: ActorSystem, EventsService: EventsService, DriverService: DriverService) {

  implicit val ec: ExecutionContext = actorSystem.dispatcher
  implicit val timeout: Timeout = Timeout(5.seconds)

  private def scheduleTask(): Unit = {
    actorSystem.scheduler.scheduleOnce(1.second) {
      MyLogger.info("[MyTask] -- Initialising MongoDB Data")
      MyLogger.blue("[MyTask][EVENTS]: Initialising events collection")

      val eventsFuture = EventsService.initialise()

      eventsFuture.flatMap { _ =>
        val eventListFuture = EventsService.getEventList

        eventListFuture.flatMap { events =>
          MyLogger.blue("[MyTask][DRIVERS/LAPS]: Initializing lap times collection")
          MyLogger.red("EVENT KEYS LIST: " + events)

          val driverServiceFuture = DriverService.addMultiple(events, batchSize = 5, delay = 1.second)

          driverServiceFuture.map { _ =>
            MyLogger.blue("[MyTask][DRIVERS]: Driver Collections updated.")
          }
        }
      }.recover {
        case ex: Throwable =>
          MyLogger.red(s"Failed to initialize lap times collection: $ex")
      }
    }
  }

  scheduleTask()
}
