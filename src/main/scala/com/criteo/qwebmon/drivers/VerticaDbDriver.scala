package com.criteo.qwebmon.drivers

import java.sql.ResultSet
import javax.sql.DataSource

import com.criteo.qwebmon.{SystemStatus, DbStatus, DbDriver}

class VerticaDbDriver(config: VerticaDbDriverConfig) extends DbDriver {

  private val dataSource = config.dataSource

  private val runningQueriesSql =
    """
      |SELECT
      |  client_hostname as client_hostname,
      |  user_name as user_name,
      |  current_statement as current_statement,
      |  DATEDIFF('second', statement_start, clock_timestamp()) as exec_time
      |FROM
      |  sessions
      |WHERE
      |  transaction_id <> -1 and
      |  statement_id is not null
      |ORDER BY
      |  exec_time desc
    """.stripMargin

  override def latestStatus: DbStatus = {
    val conn = dataSource.getConnection
    JdbcHelpers.withAutoCloseable(conn) { conn =>
      val stmt = conn.createStatement
      
      DbStatus(Seq.empty, SystemStatus(5, 4.5f, "minute"))
    }


  }

}

case class VerticaDbDriverConfig(driver: String, url: String, user: String, password: String, dataSource: DataSource)