package tasks

import org.apache.pekko.actor.ActorSystem

import javax.inject.Inject
import scala.concurrent.{Await, ExecutionContext}
import services.{DriverService, EventsService, LapsService, MyLogger}

import scala.concurrent.duration._

class MyTask @Inject()(actorSystem: ActorSystem, EventsService: EventsService, DriverService: DriverService, LapsService: LapsService) {

  implicit val ec: ExecutionContext = actorSystem.dispatcher

  private def scheduleTask(): Unit = {
    MyLogger.info("[MyTask] -- Initialising MongoDB Data")
    MyLogger.blue("[MyTask][EVENTS]: Initialising events collection")
    Await.result(EventsService.initialise(), 2.minutes)
    val events = Await.result(EventsService.getEventList, 2.minutes)

    addDrivers(events)
  }

  def addDrivers(events: Seq[Int])= {
    Await.result(DriverService.addMultiple(events), 2.minutes)

    addLaps(events)
  }

  def addLaps(events: Seq[Int])= {
    Await.result(LapsService.addMultiple(events), 4.minutes)
  }

  scheduleTask()
}
