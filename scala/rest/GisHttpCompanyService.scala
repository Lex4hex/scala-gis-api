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
  * API Service class with rest methods for companies.
  * Swagger annotations are used for API documentation
  *
  * @param modules Database and persistence modules
  */

@Api(value = "/company", description = "Operations about companies")
abstract class GisHttpCompanyService(modules: Configuration with PersistenceModule) extends HttpService {

  import JsonProtocol._
  import SprayJsonSupport._

  implicit val timeout = Timeout(5.seconds)

  @ApiOperation(httpMethod = "GET",
    value    = "Returns company by id",
    response = classOf[Company])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "companyId",
      required  = true,
      dataType  = "long",
      paramType = "path",
      value     = "ID of company that needs to be searched by")))
  @ApiResponses(Array(new ApiResponse(code = 200, message = "Ok")))
  def CompanyGetCompanyByIdRoute = path("company" / IntNumber) { (companyId) =>
    get {
      respondWithMediaType(`application/json`) {
        onComplete(modules.companyDal.findById(companyId)) {
          case Success(companies) => complete(companies)
          case Failure(ex)        => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
        }
      }
    }
  }

  @ApiOperation(httpMethod = "GET",
    value    = "Returns company by name",
    response = classOf[Company])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "companyName",
      required  = true,
      dataType  = "string",
      paramType = "path",
      value     = "ID of company that needs to be searched by")))
  @ApiResponses(Array(new ApiResponse(code = 200, message = "Ok")))
  @Path("/name/{companyName}")
  def CompanyGetCompanyByNameRoute = path("company" / "name" / Segment) { (companyName) =>
    get {
      respondWithMediaType(`application/json`) {
        onComplete(modules.companyDalImpl.findByName(companyName)) {
          case Success(companies) => complete(companies)
          case Failure(ex)        => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
        }
      }
    }
  }
}