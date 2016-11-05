package com.criteo.qwebmon.drivers

import javax.sql.DataSource

import com.criteo.qwebmon.{RunningQuery, DbDriver}

import scala.collection.mutable

class MysqlDbDriver(val name: String, dataSource: DataSource) extends DbDriver {

  private val runningQueriesSql = "show processlist"

  override def runningQueries: Seq[RunningQuery] = {
    val runningQueries: Seq[RunningQuery] = JdbcHelpers.executeQuery(dataSource, runningQueriesSql) { rs =>
      val acc = mutable.ListBuffer.empty[RunningQuery]
      while (rs.next()) {
        Option(rs.getString(8)).foreach { query =>
          acc += RunningQuery(
            user = rs.getString(2),
            runSeconds = rs.getInt(6),
            query = query,
            hostname = rs.getString(3)
          )
        }
      }
      acc
    }
    runningQueries
  }

}