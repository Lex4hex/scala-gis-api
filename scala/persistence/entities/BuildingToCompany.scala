package persistence.entities

import persistence.postgresDriver.MyPostgresDriver.api._
import slick.model.ForeignKeyAction

/** Entity class storing rows of table BuildingToCompany
  *
  * @param id         Database column id SqlType(serial), AutoInc, PrimaryKey
  * @param buildingId Database column building_id SqlType(int4)
  * @param companyId  Database column company_id SqlType(int4) */
case class BuildingToCompany(id: Int, buildingId: Int, companyId: Int) extends BaseEntity

/** Simplified entity class storing rows of table BuildingToCompany without primary key
  *
  * @param buildingId Database column building_id SqlType(int4)
  * @param companyId  Database column company_id SqlType(int4) */
case class SimpleBuildingToCompany(buildingId: Int, companyId: Int)

/** Table description of table building_to_company. Objects of this class serve as prototypes for rows in queries. */
class BuildingToCompanyTable(tag: Tag) extends BaseTable[BuildingToCompany](tag, "building_to_company") {
  /** Foreign key referencing Building (database name building_to_company_building_fk) */
  lazy val buildingFk = foreignKey("building_to_company_building_fk", buildingId, Building)(r => r.id,
    onUpdate = ForeignKeyAction.Cascade,
    onDelete = ForeignKeyAction.Cascade)

  /** Foreign key referencing Company (database name building_to_company_company_fk) */
  lazy val companyFk = foreignKey("building_to_company_company_fk", companyId, Company)(r => r.id,
    onUpdate = ForeignKeyAction.Cascade,
    onDelete = ForeignKeyAction.Cascade)

  /** Collection-like TableQuery object for table Company */
  lazy val Company         = new TableQuery(tag => new CompanyTable(tag))

  /** Collection-like TableQuery object for table Building */
  lazy val Building        = new TableQuery(tag => new BuildingTable(tag))

  /** Database column building_id SqlType(int4) */
  val buildingId: Rep[Int] = column[Int]("building_id")

  /** Database column company_id SqlType(int4) */
  val companyId: Rep[Int]  = column[Int]("company_id")

  def * = (id, buildingId, companyId) <> (BuildingToCompany.tupled, BuildingToCompany.unapply)

  /** Maps whole row to an option. Useful for outer joins. */
  def ? = (Rep.Some(id), Rep.Some(buildingId), Rep.Some(companyId)).shaped
    .<>({ r => import r._; _1.map(_ => BuildingToCompany.tupled((_1.get, _2.get, _3.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported."))

}