package persistence.entities

import persistence.postgresDriver.MyPostgresDriver.api._
import slick.model.ForeignKeyAction
import spray.json.JsValue

/** Entity class storing rows of table Company
  *
  * @param id           Database column id SqlType(serial), AutoInc, PrimaryKey
  * @param name         Database column name SqlType(varchar), Length(255,true)
  * @param phoneNumbers Database column phone_numbers SqlType(json), Length(2147483647,false)
  * @param headingId    Database column heading_id SqlType(int4) */
case class Company(id: Int, name: String, phoneNumbers: JsValue, headingId: Int) extends BaseEntity

/** Simplified entity class storing rows of table Company withoit primary key
  *
  * @param name         Database column name SqlType(varchar), Length(255,true)
  * @param phoneNumbers Database column phone_numbers SqlType(json), Length(2147483647,false)
  * @param headingId    Database column heading_id SqlType(int4) */
case class SimpleCompany(name: String, phoneNumbers: JsValue, headingId: Int)

/** Table description of table company. Objects of this class serve as prototypes for rows in queries. */
class CompanyTable(tag: Tag) extends BaseTable[Company](tag, "company") {
  /** Foreign key referencing Headingtree (database name company_headingtree_fk) */
  lazy val headingtreeFk = foreignKey("company_headingtree_fk", headingId, Headingtree)(r => r.id,
    onUpdate = ForeignKeyAction.NoAction,
    onDelete = ForeignKeyAction.NoAction)

  /** Collection-like TableQuery object for table Headingtree */
  lazy val Headingtree           = new TableQuery(tag => new HeadingTreeTable(tag))

  /** Database column name SqlType(varchar), Length(255,true) */
  val name: Rep[String]          = column[String]("name", O.Length(255, varying = true))

  /** Database column phone_numbers SqlType(json), Length(2147483647,false) */
  val phoneNumbers: Rep[JsValue] = column[JsValue]("phone_numbers")

  /** Database column heading_id SqlType(int4) */
  val headingId: Rep[Int]        = column[Int]("heading_id")

  def * = (id, name, phoneNumbers, headingId) <> (Company.tupled, Company.unapply)

  /** Maps whole row to an option. Useful for outer joins. */
  def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(phoneNumbers), Rep.Some(headingId)).shaped
    .<>({ r => import r._; _1.map(_ => Company.tupled((_1.get, _2.get, _3.get, _4.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported."))
}

