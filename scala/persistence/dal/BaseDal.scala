package persistence.dal

import persistence.entities.{BaseEntity, BaseTable}
import persistence.postgresDriver.MyPostgresDriver.api._
import slick.driver.JdbcProfile
import slick.lifted.CanBeQueryCondition
import utils.{DbModule, Profile}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Base DAL trait with basic methods
  *
  * @tparam T Table description
  * @tparam A Entity class storing rows of some table
  */

trait BaseDal[T, A] {
  def insert(row: A): Future[Int]

  def insert(rows: Seq[A]): Future[Seq[Int]]

  def update(row: A): Future[Int]

  def update(rows: Seq[A]): Future[Unit]

  def findById(id: Int): Future[Option[A]]

  def findByFilter[C: CanBeQueryCondition](f: (T) => C): Future[Seq[A]]

  def deleteById(id: Int): Future[Int]

  def deleteById(ids: Seq[Int]): Future[Int]

  def deleteByFilter[C: CanBeQueryCondition](f: (T) => C): Future[Int]

  def findAll: Future[Seq[A]]

  def createTable(): Future[Unit]
}

/**
  * Base DAL realization
  *
  * @param tableQ  TableQuery implementation to work with
  * @param db      Database connection
  * @param profile JdbcProfile
  * @tparam T Table description
  * @tparam A Entity class storing rows of some table
  */

class BaseDalImpl[T <: BaseTable[A], A <: BaseEntity](tableQ: TableQuery[T])
                                                     (implicit val db: JdbcProfile#Backend#Database,
                                                      implicit val profile: JdbcProfile)
  extends BaseDal[T, A] with Profile with DbModule {

  import profile.api._

  override def insert(row: A): Future[Int] = {
    insert(Seq(row)).map(_.head)
  }

  override def insert(rows: Seq[A]): Future[Seq[Int]] = {
    db.run(tableQ returning tableQ.map(_.id) ++= rows.filter(_.isValid))
  }

  override def update(row: A): Future[Int] = {
    if (row.isValid)
      db.run(tableQ.filter(_.id === row.id).update(row))
    else
      Future {
        0
      }
  }

  override def update(rows: Seq[A]): Future[Unit] = {
    db.run(DBIO.seq(rows.filter(_.isValid).map(r => tableQ.filter(_.id === r.id).update(r)): _*))
  }

  override def findById(id: Int): Future[Option[A]] = {
    db.run(tableQ.filter(_.id === id).result.headOption)
  }

  override def findByFilter[C: CanBeQueryCondition](f: (T) => C): Future[Seq[A]] = {
    db.run(tableQ.withFilter(f).result)
  }

  override def deleteById(id: Int): Future[Int] = {
    deleteById(Seq(id))
  }

  override def deleteById(ids: Seq[Int]): Future[Int] = {
    db.run(tableQ.filter(_.id.inSet(ids)).delete)
  }

  override def deleteByFilter[C: CanBeQueryCondition](f: (T) => C): Future[Int] = {
    db.run(tableQ.withFilter(f).delete)
  }

  override def findAll: Future[Seq[A]] = {
    db.run(tableQ.result)
  }

  override def createTable(): Future[Unit] = {
    db.run(DBIO.seq(tableQ.schema.create))
  }
}