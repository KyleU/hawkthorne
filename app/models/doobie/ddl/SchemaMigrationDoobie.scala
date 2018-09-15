/* Generated File */
package models.doobie.ddl

import cats.data.NonEmptyList
import models.ddl.SchemaMigration
import models.doobie.DoobieQueries
import services.database.DoobieQueryService.Imports._

object SchemaMigrationDoobie extends DoobieQueries[SchemaMigration]("flyway_schema_history") {
  override protected val countFragment = fr"""select count(*) from "flyway_schema_history""""
  override protected val selectFragment = fr"""select "installed_rank", "version", "description", "type", "script", "checksum", "installed_by", "installed_on", "execution_time", "success" from "flyway_schema_history""""

  override protected val columns = Seq("installed_rank", "version", "description", "type", "script", "checksum", "installed_by", "installed_on", "execution_time", "success")
  override protected val searchColumns = Seq("installed_rank", "version", "description", "type", "success")

  override protected def searchFragment(q: String) = {
    fr""""installed_rank"::text = $q or "version"::text = $q or "description"::text = $q or "type"::text = $q or "script"::text = $q or "checksum"::text = $q or "installed_by"::text = $q or "installed_on"::text = $q or "execution_time"::text = $q or "success"::text = $q"""
  }

  def getByPrimaryKey(installedRank: Long) = (selectFragment ++ whereAnd(fr"installedRank = $installedRank")).query[Option[SchemaMigration]].unique
  def getByPrimaryKeySeq(installedRankSeq: NonEmptyList[Long]) = (selectFragment ++ in(fr"installedRank", installedRankSeq)).query[SchemaMigration].to[Seq]
}

