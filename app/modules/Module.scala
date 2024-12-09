package modules

import com.google.inject.{AbstractModule, Provides}
import connectors.{ApiClient, F1OpenApiClient}
import models._
import org.mongodb.scala.bson.codecs.Macros
import play.api.inject.ApplicationLifecycle
import tasks.MyTask
import repositories.{MongoDbConnectionManager, MongoCollectionWrapper}

import scala.concurrent.{ExecutionContext, Future}

class Module
  extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ApiClient]).to(classOf[F1OpenApiClient])
    bind(classOf[MyTask]).asEagerSingleton()
  }

  def setupLifecycleHook(lifecycle: ApplicationLifecycle): Unit = {
    lifecycle.addStopHook { () =>
      Future.successful(MongoDbConnectionManager.close())
    }
  }
}

object Module {
  @Provides
  def provideEventsConnection(implicit ec: ExecutionContext): MongoCollectionWrapper[Event] =
    new MongoCollectionWrapper[Event]("events", Macros.createCodecProvider[Event]())

  @Provides
  def provideDriversConnection(implicit ec: ExecutionContext): MongoCollectionWrapper[Drivers] =
    new MongoCollectionWrapper[Drivers]("drivers", Macros.createCodecProvider[Drivers]())

  @Provides
  def provideLapsConnection(implicit ec: ExecutionContext): MongoCollectionWrapper[Laps] =
    new MongoCollectionWrapper[Laps]("laps", Macros.createCodecProvider[Laps]())
}