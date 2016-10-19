package persistence.postgresDriver

import com.github.tminglei.slickpg._

/**
  * Custom Postgres Driver for slick-pg features
  */

trait MyPostgresDriver extends ExPostgresDriver
  with PgArraySupport
  with PgDateSupport
  with PgJsonSupport
  with PgNetSupport
  with PgLTreeSupport
  with PgRangeSupport
  with PgHStoreSupport
  with PgSearchSupport
  with PgSprayJsonSupport
  with PgPostGISSupport {

  override val pgjson = "jsonb"

  override val api    = new API with ArrayImplicits
    with DateTimeImplicits
    with JsonImplicits
    with NetImplicits
    with LTreeImplicits
    with RangeImplicits
    with HStoreImplicits
    with SearchImplicits
    with SearchAssistants
    with SprayJsonImplicits
    with PostGISImplicits with PostGISAssistants {}
}

object MyPostgresDriver extends MyPostgresDriver