package persistence.dal

import persistence.entities.{Company, CompanyTable}
import persistence.postgresDriver.MyPostgresDriver.api._
import slick.driver.JdbcProfile
import utils.{DbModule, Profile}

import scala.concurrent.Future

/**
  * Special DAL functionality for companies
  *
  * @param company TableQuery implementation for Building
  * @param db      Database connection
  * @param profile JdbcProfile
  */

class CompanyDalImpl(company: TableQuery[CompanyTable])(implicit val db: JdbcProfile#Backend#Database,
                                                        implicit val profile: JdbcProfile) extends Profile with DbModule {

  def findByName(name: String): Future[Option[Company]] = {
    db.run(company.filter(_.name === name).result.headOption)
  }
}
