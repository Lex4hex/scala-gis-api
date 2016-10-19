package rest

import javax.ws.rs.Path

import akka.util.Timeout
import com.wordnik.swagger.annotations._
import entities.JsonProtocol
import persistence.entities._
import spray.http.MediaTypes._
import spray.http.StatusCodes._
import spray.httpx.SprayJsonSupport
import spray.routing._
import utils.{Configuration, PersistenceModule}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * API Service class with rest methods for buildings.
  * Swagger annotations are used for API documentation
  *
  * @param modules Database and persistence modules
  */

@Api(value = "/building", description = "Operations about buildings")
abstract class GisHttpBuildingService(modules: Configuration with PersistenceModule) extends HttpService {

  import JsonProtocol._
  import SprayJsonSupport._

  implicit val timeout = Timeout(5.seconds)

  @ApiOperation(httpMethod = "GET",
    value    = "Returns all companies based on building ID",
    response = classOf[List[Company]])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "buildingId",
      required  = true,
      dataType  = "long",
      paramType = "path",
      value     = "ID of building that needs to be searched by")))
  @ApiResponses(Array(new ApiResponse(code = 200, message = "Ok")))
  @Path("/{buildingID}/companies")
  def BuildingGetAllCompaniesRoute = path("building" / IntNumber / "companies") { (buildingID) =>
    get {
      respondWithMediaType(`application/json`) {
        onComplete(modules.buildingDalImpl.findAllCompaniesByBuilding(buildingID)) {
          case Success(companies) => complete(companies)
          case Failure(ex)        => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
        }
      }
    }
  }

  @ApiOperation(httpMethod = "GET",
    value    = "Returns all buildings",
    response = classOf[List[Building]])
  @ApiResponses(Array(new ApiResponse(code = 200, message = "Ok")))
  def BuildingGetRoute = path("building") {
    get {
      respondWithMediaType(`application/json`) {
        onComplete(modules.buildingDal.findAll) {
          case Success(buildings) => complete(buildings)
          case Failure(ex)        => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
        }
      }
    }
  }
}