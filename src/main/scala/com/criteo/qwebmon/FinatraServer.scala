package com.criteo.qwebmon

import com.criteo.qwebmon.drivers.FakeDbDriver
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter

object FinatraServerMain extends FinatraServer {
  override def dbDrivers: Map[String, DbDriver] = Map(
    "fake-db" -> new FakeDbDriver
  )
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
