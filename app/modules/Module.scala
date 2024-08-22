package modules

import com.google.inject.{AbstractModule, Provides}
import config.MongoDbConnection
import connectors.{ApiClient, RealApiClient}
import models._
import org.mongodb.scala.bson.codecs.Macros
import tasks.MyTask
import play.api.{Configuration, Environment}

import scala.concurrent.ExecutionContext

class Module(environment: Environment, configuration: Configuration)
  extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ApiClient]).to(classOf[RealApiClient])
    bind(classOf[MyTask]).asEagerSingleton()
  }
}

object Module {
  @Provides
  def provideEventsConnection(implicit ec: ExecutionContext): MongoDbConnection[Event] =
    new MongoDbConnection[Event]("events", Macros.createCodecProvider[Event]())

  @Provides
  def provideDriversConnection(implicit ec: ExecutionContext): MongoDbConnection[Drivers] =
    new MongoDbConnection[Drivers]("drivers", Macros.createCodecProvider[Drivers]())

  @Provides
  def provideLapsConnection(implicit ec: ExecutionContext): MongoDbConnection[Laps] =
    new MongoDbConnection[Laps]("laps", Macros.createCodecProvider[Laps]())
}