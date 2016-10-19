package persistence.entities

import persistence.postgresDriver.MyPostgresDriver.api._
import slick.lifted.Tag

/**
  * Base table which should be extended by other table classes
  *
  * @param tag  Table name on SQL level
  * @param name Table name for tag
  * @tparam T Table description class
  */

abstract class BaseTable[T](tag: Tag, name: String) extends Table[T](tag, name) {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
}