package tasks

import org.apache.pekko.actor.ActorSystem
import javax.inject.Inject
import scala.concurrent.ExecutionContext
import services.{DriverService, EventsService, LapsService, MyLogger}

import scala.concurrent.duration._

class MyTask @Inject()(actorSystem: ActorSystem, EventsService: EventsService, DriverService: DriverService, LapsService: LapsService) {

  implicit val ec: ExecutionContext = actorSystem.dispatcher

  private def scheduleTask(): Unit = {
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
            scheduleLapsTask(events)
            MyLogger.blue("[MyTask][DRIVERS]: Driver Collections updated.")
          }
        }
      }.recover {
        case ex: Throwable =>
          MyLogger.red(s"Failed to initialize lap times collection: $ex")
      }
  }

  private def scheduleLapsTask(events: Seq[Int]): Unit = {
        MyLogger.blue("[MyTask][LAPS]: Initializing lap times collection")
        val lapsServiceFuture = LapsService.addMultiple(events, batchSize = 5, delay = 1.second)

        lapsServiceFuture.map { _ =>
          MyLogger.blue("[MyTask][LAPS]: LAPS Collections updated.")
        }.recover {
      case ex: Throwable =>
        MyLogger.red(s"Failed to initialize lap times collection: $ex")
    }
  }

  scheduleTask()
}
