package persistence.entities

import com.github.tminglei.slickpg.LTree
import persistence.postgresDriver.MyPostgresDriver.api._

/** Entity class storing rows of table Headingtree
  *
  * @param id   Database column id SqlType(serial), AutoInc, PrimaryKey
  * @param path Database column path SqlType(ltree), Length(2147483647,false)
  * @param name Database column name SqlType(varchar), Length(255,true) */
case class HeadingTree(id: Int, path: LTree, name: String) extends BaseEntity

/** Simplified entity class storing rows of table Headingtree without primary key
  *
  * @param path Database column path SqlType(ltree), Length(2147483647,false)
  * @param name Database column name SqlType(varchar), Length(255,true) */
case class SimpleHeadingTree(path: LTree, name: String)

/** Table description of table headingtree. Objects of this class serve as prototypes for rows in queries. */
class HeadingTreeTable(tag: Tag) extends BaseTable[HeadingTree](tag, "headingtree") {
  /** Database column path SqlType(ltree), Length(2147483647,false) */
  val path: Rep[LTree]  = column[LTree]("path")

  /** Database column name SqlType(varchar), Length(255,true) */
  val name: Rep[String] = column[String]("name", O.Length(255, varying = true))

  /** Index over (path) (database name headingtree_gist_index) */
  val index1            = index("headingtree_gist_index", path)

  def * = (id, path, name) <> (HeadingTree.tupled, HeadingTree.unapply)

  /** Maps whole row to an option. Useful for outer joins. */
  def ? = (Rep.Some(id), Rep.Some(path), Rep.Some(name)).shaped
    .<>({ r => import r._; _1.map(_ => HeadingTree.tupled((_1.get, _2.get, _3.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported."))
}