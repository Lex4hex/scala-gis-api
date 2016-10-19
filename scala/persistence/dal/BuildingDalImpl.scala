package persistence.dal

import persistence.entities._
import persistence.postgresDriver.MyPostgresDriver.api._
import slick.driver.JdbcProfile
import utils.{DbModule, Profile}

import scala.concurrent.Future

/**
  * Special DAL functionality for buildings
  *
  * @param building TableQuery implementation for Building
  * @param db       Database connection
  * @param profile  JdbcProfile
  */

class BuildingDalImpl(building: TableQuery[BuildingTable])(implicit val db: JdbcProfile#Backend#Database,
                                                           implicit val profile: JdbcProfile)
  extends Profile with DbModule {

  val defaultSRID = 4326

  /**
    * Returns all companies in provided radius of some point
    *
    * @param long   Longitude
    * @param lat    Latitude
    * @param radius Radius to search in
    */
  def findAllCompaniesInRadius(long: Double, lat: Double, radius: Float): Future[Seq[Company]] = {
    val companies         = TableQuery[CompanyTable]
    val buildingToCompany = TableQuery[BuildingToCompanyTable]
    val query = (for (
      buildingInPoint <- building if makePoint(buildingInPoint.longitude.asColumnOf[Double],
      buildingInPoint.latitude.asColumnOf[Double])
      .setSRID(defaultSRID).distanceSphere(makePoint(long, lat).setSRID(defaultSRID)) <
      radius;
      btc <- buildingToCompany if buildingInPoint.id === btc.buildingId;
      comps <- companies if btc.companyId === comps.id
    ) yield comps).result

    db.run(query)
  }

  def findAllCompaniesByBuilding(id: Int): Future[Seq[Company]] = {
    val companies = TableQuery[CompanyTable]
    val buildingToCompany = TableQuery[BuildingToCompanyTable]

    val query = (for (
      builToComp <- buildingToCompany if builToComp.buildingId === id;
      comps <- companies if builToComp.companyId === comps.id
    ) yield comps).result

    db.run(query)
  }

  def findAllBuildings: Future[Seq[Building]] = {
    db.run(building.result)
  }

}
