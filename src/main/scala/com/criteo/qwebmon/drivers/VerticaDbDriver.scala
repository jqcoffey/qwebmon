package com.criteo.qwebmon.drivers

import javax.sql.DataSource

import com.criteo.qwebmon.{RunningQuery, DbDriver}
import com.mchange.v2.c3p0.ComboPooledDataSource
import com.typesafe.config.Config

import scala.collection.mutable

class VerticaDbDriver(val name: String, dataSource: DataSource) extends DbDriver {

  private val runningQueriesSql =
    """
      |SELECT
      |  user_name as user_name,
      |  DATEDIFF('second', statement_start, clock_timestamp()) as exec_time,
      |  current_statement as current_statement,
      |  client_hostname as client_hostname
      |FROM
      |  sessions
      |WHERE
      |  transaction_id <> -1 and
      |  statement_id is not null
      |ORDER BY
      |  exec_time desc
    """.stripMargin

  override def runningQueries: Seq[RunningQuery] = {
    val runningQueries: Seq[RunningQuery] = JdbcHelpers.executeQuery(dataSource, runningQueriesSql) { rs =>
      val acc = mutable.ListBuffer.empty[RunningQuery]
      while (rs.next()) {
        acc += RunningQuery(
          user = rs.getString(1),
          runSeconds = rs.getInt(2),
          query = rs.getString(3),
          hostname = rs.getString(4)
        )
      }
      acc
    }
    runningQueries
  }

}

case class VerticaDbDriverConfig(target: String, dataSource: DataSource)

case object VerticaDbDriverConfig {

  def apply(config: Config, target: String, dataSource: ComboPooledDataSource): VerticaDbDriverConfig = {
    val url = config.getString(s"vertica.$target.url")
    val user = config.getString(s"vertica.$target.user")
    val password = config.getString(s"vertica.$target.password")

    dataSource.setDriverClass("com.vertica.jdbc.Driver")
    dataSource.setJdbcUrl(url)
    dataSource.setUser(user)
    dataSource.setPassword(password)

    VerticaDbDriverConfig(
      target = target,
      dataSource = dataSource
    )
  }

}