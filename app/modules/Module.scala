package modules

import com.google.inject.{AbstractModule, Provides}
import connectors.{ApiClient, F1OpenApiClient}
import models._
import org.mongodb.scala.bson.codecs.Macros
import play.api.inject.ApplicationLifecycle
import tasks.MyTask
import repositories.{MongoDbConnectionManager, MongoDbFactory}

import scala.concurrent.{ExecutionContext, Future}

class Module
  extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ApiClient]).to(classOf[F1OpenApiClient])
    bind(classOf[MyTask]).asEagerSingleton()
  }

  @Provides
  def stopMongoClient(lifecycle: ApplicationLifecycle): Unit = {
    lifecycle.addStopHook { () =>
      Future.successful(MongoDbFactory.close())
    }
  }
}

object Module {
  @Provides
  def provideEventsConnection(implicit ec: ExecutionContext): MongoDbConnectionManager[Event] =
    new MongoDbConnectionManager[Event]("events", Macros.createCodecProvider[Event]())

  @Provides
  def provideDriversConnection(implicit ec: ExecutionContext): MongoDbConnectionManager[Drivers] =
    new MongoDbConnectionManager[Drivers]("drivers", Macros.createCodecProvider[Drivers]())

  @Provides
  def provideLapsConnection(implicit ec: ExecutionContext): MongoDbConnectionManager[Laps] =
    new MongoDbConnectionManager[Laps]("laps", Macros.createCodecProvider[Laps]())
}