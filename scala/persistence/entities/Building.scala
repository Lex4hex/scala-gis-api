package persistence.entities

import persistence.postgresDriver.MyPostgresDriver.api._

/** Entity class storing rows of table Building
  *
  * @param id        Database column id SqlType(serial), AutoInc, PrimaryKey
  * @param address   Database column address SqlType(varchar), Length(255,true)
  * @param longitude Database column longitude SqlType(float4)
  * @param latitude  Database column latitude SqlType(float4) */
case class Building(id: Int, address: String, longitude: Float, latitude: Float) extends BaseEntity

/** Simplified entity class storing rows of table Building without primary key
  *
  * @param address   Database column address SqlType(varchar), Length(255,true)
  * @param longitude Database column longitude SqlType(float4)
  * @param latitude  Database column latitude SqlType(float4) */
case class SimpleBuilding(address: String, longitude: Float, latitude: Float)

/** Table description of table building. Objects of this class serve as prototypes for rows in queries. */
class BuildingTable(tag: Tag) extends BaseTable[Building](tag, "building") {
  /** Database column address SqlType(varchar), Length(255,true) */
  val address: Rep[String]  = column[String]("address", O.Length(255, varying = true))

  /** Database column longitude SqlType(float4) */
  val longitude: Rep[Float] = column[Float]("longitude")

  /** Database column latitude SqlType(float4) */
  val latitude: Rep[Float]  = column[Float]("latitude")

  /** Uniqueness Index over (longitude,latitude) (database name buildingUniqueLongLatKey) */
  val index1                = index("buildingUniqueLongLatKey", (longitude, latitude), unique = true)

  /** Uniqueness Index over (address,longitude,latitude) (database name buildingUniquePosition) */
  val index2                = index("buildingUniquePosition", (address, longitude, latitude), unique = true)

  def * = (id, address, longitude, latitude) <> (Building.tupled, Building.unapply)

  /** Maps whole row to an option. Useful for outer joins. */
  def ? = (Rep.Some(id), Rep.Some(address), Rep.Some(longitude), Rep.Some(latitude)).shaped
    .<>({ r => import r._; _1.map(_ => Building.tupled((_1.get, _2.get, _3.get, _4.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported."))
}