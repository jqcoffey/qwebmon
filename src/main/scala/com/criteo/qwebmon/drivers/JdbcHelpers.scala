package com.criteo.qwebmon.drivers

import java.sql.{ResultSet, Statement, Connection}
import javax.sql.DataSource

object JdbcHelpers {

  /**
    * Applies a function of type A => R on an AutoCloseable object of type A.
    *
    * @param autoCloseable the object with the AutoCloseable trait
    * @param fn
    * @tparam A
    * @tparam R
    * @return
    */
  def withAutoCloseable[A <: AutoCloseable, R](autoCloseable: A)(fn: (A => R)): R = {
    try {
      fn(autoCloseable)
    } finally {
      autoCloseable.close()
    }
  }

  /**
    * Extracts a Connection from a DataSource and safely applies a transformation fn Connection => R
    *
    * @param ds
    * @param fn
    * @tparam R
    * @return
    */
  def withConnectionFrom[R](ds: DataSource)(fn: (Connection => R)): R = {
    withAutoCloseable(ds.getConnection)(fn)
  }

  /**
    * Extracts a Statement from a DataSource and safely applies a transformation fn Statement => R
    * @param ds
    * @param fn
    * @tparam R
    * @return
    */
  def withStatementFrom[R](ds: DataSource)(fn: (Statement => R)): R = {
    withConnectionFrom(ds) { conn =>
      withAutoCloseable(conn.createStatement())(fn)
    }
  }

  /**
    * Executes the supplied SQL against the supplied DataSource safely and transforms the ResultSet => R.
    * @param ds
    * @param sql
    * @param fn
    * @tparam R
    * @return
    */
  def executeQuery[R](ds: DataSource, sql: String)(fn: (ResultSet => R)): R = {
    withStatementFrom(ds) { stmt =>
      withAutoCloseable(stmt.executeQuery(sql))(fn)
    }
  }
}
