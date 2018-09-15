/* Generated File */
package models.doobie.ddl

import models.ddl.SchemaMigration
import models.doobie.DoobieTestHelper
import org.scalatest._
import services.database.DoobieQueryService.Imports._

class SchemaMigrationDoobieTest extends FlatSpec with Matchers {
  import DoobieTestHelper.yolo._

  "Doobie queries for [SchemaMigration]" should "typecheck" in {
    SchemaMigrationDoobie.countFragment.query[Long].check.unsafeRunSync
    SchemaMigrationDoobie.selectFragment.query[SchemaMigration].check.unsafeRunSync
    (SchemaMigrationDoobie.selectFragment ++ whereAnd(SchemaMigrationDoobie.searchFragment("..."))).query[SchemaMigration].check.unsafeRunSync
  }
}
