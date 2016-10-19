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
  * API Service class with rest methods for locations.
  * Swagger annotations are used for API documentation
  *
  * @param modules Database and persistence modules
  */

@Api(value = "/location", description = "Operations about location")
abstract class GisHttpLocationService(modules: Configuration with PersistenceModule) extends HttpService {

  import JsonProtocol._
  import SprayJsonSupport._

  implicit val timeout = Timeout(5.seconds)

  @ApiOperation(httpMethod = "GET",
    value    = "Returns all companies in area by coordinates and radius",
    response = classOf[List[Company]])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "Longitude",
      required  = true,
      dataType  = "double",
      paramType = "path",
      value     = "Longitude in EPSG:4326 WGS 84 / Latlong format"),
    new ApiImplicitParam(name = "Latitude",
      required  = true,
      dataType  = "double",
      paramType = "path",
      value     = "Latitude EPSG:4326 WGS 84 / Latlong"),
    new ApiImplicitParam(name = "Radius",
      required  = true,
      dataType  = "double",
      paramType = "path",
      value     = "Radius in meters to locate companies")))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Ok"),
    new ApiResponse(code = 404, message = "Companies was not found in selected area")))
  @Path("/{Longitude}/{Latitude}/{Radius}")
  def LocationGetAllByCoordinatesRoute = path("location" / DoubleNumber / DoubleNumber / DoubleNumber) {
    (long, lat, radius) =>
      get {
        respondWithMediaType(`application/json`) {
          onComplete(modules.buildingDalImpl.findAllCompaniesInRadius(long, lat, radius.toFloat)) {
            case Success(companies) => complete(companies)
            case Failure(ex)        => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
          }
        }
      }
  }
}