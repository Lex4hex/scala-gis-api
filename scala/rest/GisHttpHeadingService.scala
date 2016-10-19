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
  * API Service class with rest methods for headings.
  * Swagger annotations are used for API documentation
  *
  * @param modules Database and persistence modules
  */

@Api(value = "/heading", description = "Operations about headings")
abstract class GisHttpHeadingService(modules: Configuration with PersistenceModule) extends HttpService {

  import JsonProtocol._
  import SprayJsonSupport._

  implicit val timeout = Timeout(5.seconds)

  @ApiOperation(httpMethod = "GET",
    value    = "Returns all companies based on heading ID",
    response = classOf[List[HeadingTree]])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "headingId",
      required  = true,
      dataType  = "long",
      paramType = "path",
      value     = "ID of heading that needs to be searched by")))
  @ApiResponses(Array(new ApiResponse(code = 200, message = "Ok")))
  @Path("/{headingId}/companies")
  def HeadingGetAllCompaniesRoute = path("heading" / IntNumber / "companies") { (headingId) =>
    get {
      respondWithMediaType(`application/json`) {
        onComplete(modules.headingTreeDalImpl.findAllCompaniesByHeadingId(headingId)) {
          case Success(companies) => complete(companies)
          case Failure(ex)        => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
        }
      }
    }
  }
}
