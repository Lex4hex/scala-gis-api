package persistence.dal

import persistence.entities.{Company, CompanyTable, HeadingTreeTable}
import persistence.postgresDriver.MyPostgresDriver.api._
import slick.driver.JdbcProfile
import utils.{DbModule, Profile}

import scala.concurrent.Future

/**
  * Special DAL functionality for Heading Tree
  *
  * @param heading TableQuery implementation for Building
  * @param db      Database connection
  * @param profile JdbcProfile
  */

class HeadingTreeDalImpl(heading: TableQuery[HeadingTreeTable])(implicit val db: JdbcProfile#Backend#Database,
                                                                implicit val profile: JdbcProfile)
  extends Profile with DbModule {

  def findAllCompaniesByHeadingId(id: Int): Future[Seq[Company]] = {
    val companies = TableQuery[CompanyTable]

    val query = (for (head <- heading if head.id === id;
                      company <- companies if head.id === company.headingId
    ) yield company).result

    db.run(query)
  }
}
