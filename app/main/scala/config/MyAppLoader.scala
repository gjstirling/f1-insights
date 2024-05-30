package main.scala.config

import play.api.ApplicationLoader.Context
import play.api._

import scala.annotation.unused

@unused
class MyApplicationLoader extends ApplicationLoader {
  override def load(context: Context): Application = {
    new MyComponents(context).application
  }
}