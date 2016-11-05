package com.criteo.qwebmon

import java.util.Properties

import com.criteo.qwebmon.drivers.{VerticaDbDriver, MysqlDbDriver, FakeDbDriver}
import com.mchange.v2.c3p0.ComboPooledDataSource
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import com.typesafe.config._
import scala.collection.JavaConverters._
import scala.collection.mutable

object FinatraServerMain extends FinatraServer {

  val config = ConfigFactory.load()

  private val _dbDrivers = mutable.Map[String, DbDriver](
    FakeDbDriver.name -> FakeDbDriver
  )

  config.getObject("targets").entrySet().asScala.foreach { entry =>
    val targetName = entry.getKey
    val targetConfig = entry.getValue match {
      case c if c.valueType() == ConfigValueType.OBJECT => c.asInstanceOf[ConfigObject].toConfig
      case x => sys.error(s"illegal config syntax at $x")
    }

    println(s"have targetConfig: $targetName, $targetConfig")

    targetConfig.getString("driver") match {
      case "mysql" => _dbDrivers += targetName -> buildMysqlDbDriver(targetName, targetConfig)
      case "vertica" => _dbDrivers += targetName -> buildVerticaDbDriver(targetName, targetConfig)
      case x => sys.error(s"unknown driver supplied: $x, for target named: $targetName, with config: $targetConfig")
    }

  }

  override def dbDrivers: Map[String, DbDriver] = _dbDrivers.toMap

  private def buildMysqlDbDriver(name: String, config: Config): MysqlDbDriver = {
    println(s"configuring mysql target: $name, $config")

    val dataSource = new ComboPooledDataSource()
    dataSource.setDriverClass("com.mysql.cj.jdbc.Driver")
    dataSource.setJdbcUrl(config.getString("url"))
    dataSource.setUser(config.getString("user"))
    dataSource.setPassword(config.getString("password"))
    new MysqlDbDriver(name, dataSource)
  }

  private def buildVerticaDbDriver(name: String, config: Config): VerticaDbDriver = {
    println(s"configuring mysql target: $name, $config")

    val dataSource = new ComboPooledDataSource()

    val connectionProps = new Properties()
    connectionProps.setProperty("ConnSettings", "SET ROLE pseudosuperuser")

    dataSource.setProperties(connectionProps)

    dataSource.setDriverClass("com.vertica.jdbc.Driver")
    dataSource.setJdbcUrl(config.getString("url"))
    dataSource.setUser(config.getString("user"))
    dataSource.setPassword(config.getString("password"))
    new VerticaDbDriver(name, dataSource)
  }
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
