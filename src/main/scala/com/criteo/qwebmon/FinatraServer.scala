package com.criteo.qwebmon

import com.criteo.qwebmon.drivers.DbDriversBuilder
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import com.typesafe.config._

object FinatraServerMain extends FinatraServer {

  private val config = ConfigFactory.load()

  override val dbDrivers: Map[String, DbDriver] = new DbDriversBuilder(config).dbDrivers

}

abstract class FinatraServer extends HttpServer {

  def dbDrivers: Map[String, DbDriver]

  def qwebmonController: QwebmonController = new QwebmonController(dbDrivers)

  def staticController: StaticController = new StaticController

  override def configureHttp(router: HttpRouter) {
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .add(qwebmonController)
      .add(staticController)
  }
}
