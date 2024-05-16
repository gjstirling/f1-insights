package main.scala.config

import org.mongodb.scala._

import javax.inject.Singleton

@Singleton
class MongoDbConnection {
  val client: MongoClient = MongoClient()
}