package modules

import com.google.inject.AbstractModule
import connectors.{ApiClient, RealApiClient}
import tasks.MyTask
import play.api.{Configuration, Environment}
import services.MyLogger

class Module(environment: Environment, configuration: Configuration)
  extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[ApiClient]).to(classOf[RealApiClient])
    bind(classOf[MyTask]).asEagerSingleton()
  }

}