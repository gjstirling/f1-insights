package modules

import com.google.inject.AbstractModule
import connectors.{ApiClient, RealApiClient}
//import services.{MongoClient, MongoClientConnection}
import play.api.{Configuration, Environment}

class Module(environment: Environment, configuration: Configuration)
  extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[ApiClient]).to(classOf[RealApiClient])
  }

}