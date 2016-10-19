package utils

import persistence.dal.{BuildingDalImpl, HeadingTreeDalImpl, _}
import persistence.entities._
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import slick.lifted.TableQuery


trait Profile {
  val profile: JdbcProfile
}


trait DbModule extends Profile {
  val db: JdbcProfile#Backend#Database
}

/**
  * All DALs
  */

trait PersistenceModule {
  val buildingDal: BaseDal[BuildingTable, Building]
  val companyDal: BaseDal[CompanyTable, Company]
  val headingTreeDal: BaseDal[HeadingTreeTable, HeadingTree]
  val companyDalImpl: CompanyDalImpl
  val buildingDalImpl: BuildingDalImpl
  val headingTreeDalImpl: HeadingTreeDalImpl
}

/**
  * Main database and dal modules implementation
  */

trait PersistenceModuleImpl extends PersistenceModule with DbModule {
  this: Configuration =>

  override implicit val profile: JdbcProfile = dbConfig.driver
  override implicit val db: JdbcProfile#Backend#Database = dbConfig.db

  override val buildingDal        = new BaseDalImpl[BuildingTable, Building](TableQuery[BuildingTable])
  override val companyDal         = new BaseDalImpl[CompanyTable, Company](TableQuery[CompanyTable])
  override val headingTreeDal     = new BaseDalImpl[HeadingTreeTable, HeadingTree](TableQuery[HeadingTreeTable])
  override val companyDalImpl     = new CompanyDalImpl(TableQuery[CompanyTable])
  override val headingTreeDalImpl = new HeadingTreeDalImpl(TableQuery[HeadingTreeTable])
  override val buildingDalImpl    = new BuildingDalImpl(TableQuery[BuildingTable])
  val self = this
  // use an alternative database configuration ex:
  private val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("pgdb")
}