package main.scala.modules

import com.google.inject.AbstractModule
import services.{ApiClient, RealApiClient}

class AppModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ApiClient]).to(classOf[RealApiClient])
  }
}
