//package main.scala.config
//
//import play.api.routing.Router
//import play.filters.HttpFiltersComponents
//import main.scala.controllers._
//import main.scala.repositories.{EventRepository, F1OpenApi}
//import router.Routes
//import services.RealApiClient
//import play.api.ApplicationLoader.Context
//import play.api._
//import play.controllers.AssetsComponents
//import play.api.Configuration
//
//import scala.annotation.unused
//
//@unused
//class MyComponents(context: Context)
//  extends BuiltInComponentsFromContext(context)
//    with HttpFiltersComponents
//    with AssetsComponents {
//
//  lazy val myAppConfig = new MyAppConfig
//  lazy val f1OpenApi = new F1OpenApi(new RealApiClient(myAppConfig))
//  lazy val eventRepository = new EventRepository(myAppConfig, new MongoDbConnection)
//
//  lazy val homeController = new HomeController(controllerComponents)
//  lazy val qualifyingLapsController = new QualifyingLapsController(controllerComponents, f1OpenApi, myAppConfig)
//  lazy val eventController = new EventController(controllerComponents, eventRepository, f1OpenApi)
//
//  override lazy val router: Router = new Routes(
//    httpErrorHandler,
//    homeController,
//    qualifyingLapsController,
//    eventController,
//    assets
//  )
//
//  override lazy val config: Configuration = configuration
//}