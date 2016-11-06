package com.criteo.qwebmon.drivers

import java.util.Properties
import javax.sql.DataSource

import com.criteo.qwebmon.DbDriver
import com.mchange.v2.c3p0.ComboPooledDataSource
import com.typesafe.config.{Config, ConfigObject, ConfigValueType}

import scala.collection.JavaConverters._

class DbDriversBuilder(config: Config) {

  val dbDrivers: Map[String, DbDriver] = config.getObject("targets")
    .entrySet().asScala
    .foldLeft(Map.empty[String, DbDriver]) { case (acc, entry) =>

    val targetName = entry.getKey
    val targetConfig = entry.getValue match {
      case c if c.valueType() == ConfigValueType.OBJECT => c.asInstanceOf[ConfigObject].toConfig
      case x => sys.error(s"illegal config syntax at $x")
    }

    targetConfig.getString("driver") match {
      case "fake-db" => acc + (targetName -> new FakeDbDriver(targetName))
      case "mysql" => acc + (targetName -> new MysqlDbDriver(targetName, buildDataSource(targetConfig)))
      case "vertica" => acc + (targetName -> new VerticaDbDriver(targetName, buildDataSource(targetConfig)))
      case x => sys.error(s"unknown driver supplied: $x, for target named: $targetName, with config: $targetConfig")
    }
  }

  private def buildDataSource(config: Config): DataSource = {
    val dataSource = new ComboPooledDataSource()

    if (config.hasPath("properties")) {
      val connectionProps = new Properties()
      config.getConfig("properties").entrySet().asScala.foreach { entry =>
        val key = entry.getKey
        val value = entry.getValue.unwrapped().toString

        connectionProps.setProperty(key, value)
      }
      dataSource.setProperties(connectionProps)
    }

    dataSource.setJdbcUrl(config.getString("url"))
    dataSource.setUser(config.getString("user"))
    dataSource.setPassword(config.getString("password"))

    dataSource
  }
}
