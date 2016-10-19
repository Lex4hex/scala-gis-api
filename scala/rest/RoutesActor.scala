package rest

import akka.actor.Actor
import akka.util.Timeout
import com.gettyimages.spray.swagger._
import com.typesafe.scalalogging.LazyLogging
import com.wordnik.swagger.model.ApiInfo
import spray.routing._
import utils.{Configuration, PersistenceModule}

import scala.concurrent.duration._
import scala.reflect.runtime.universe._

/**
  * Main Actor with all API routes
  *
  * @param modules Database and persistence modules
  */

class RoutesActor(modules: Configuration with PersistenceModule) extends Actor with HttpService with LazyLogging {

  implicit val timeout = Timeout(5.seconds)
  val swaggerService = new SwaggerHttpService {
    override def apiTypes = Seq(
      typeOf[GisHttpBuildingService],
      typeOf[GisHttpHeadingService],
      typeOf[GisHttpCompanyService],
      typeOf[GisHttpLocationService])

    override def apiVersion = "1.0"

    override def baseUrl = "/"

    override def docsPath = "api-docs"

    override def actorRefFactory = context

    override def apiInfo = Some(ApiInfo("GIS API",
      "Scala, spray, swagger, akka GIS API to work with companies data",
      "",
      "Alexey Zotov lexzotov@gmail.com",
      "MIT License",
      "https://opensource.org/licenses/MIT"))
  }
  val buildings = new GisHttpBuildingService(modules) {
    def actorRefFactory = context
  }
  val headings = new GisHttpHeadingService(modules) {
    def actorRefFactory = context
  }
  val companies = new GisHttpCompanyService(modules) {
    def actorRefFactory = context
  }
  val locations = new GisHttpLocationService(modules) {
    def actorRefFactory = context
  }

  def actorRefFactory = context

  /**
    * All API routes
    */
  def receive = runRoute(buildings.BuildingGetRoute ~
    buildings.BuildingGetAllCompaniesRoute ~
    headings.HeadingGetAllCompaniesRoute ~
    companies.CompanyGetCompanyByIdRoute ~
    companies.CompanyGetCompanyByNameRoute ~
    locations.LocationGetAllByCoordinatesRoute ~
    swaggerService.routes ~
    get {
      pathPrefix("") {
        pathEndOrSingleSlash {
          getFromResource("swagger-ui/index.html")
        }
      } ~
        getFromResourceDirectory("swagger-ui")
    })
}
