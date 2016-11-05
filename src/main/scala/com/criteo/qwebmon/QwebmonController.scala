package com.criteo.qwebmon

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

class QwebmonController(drivers: Map[String, DbDriver]) extends Controller {

  get("/refresh/:target") { r: Request =>
    val targetName = r.getParam("target")
    drivers.get(targetName).map { driver =>
      val runningQueries = driver.runningQueries
      DbStatus(runningQueries, runningQueries.length)
    }.getOrElse(response.badRequest(s"invalid target supplied: $targetName"))
  }

  get("/") { r: Request =>
    response.ok.file("/index.html")
  }

}

case class DbStatus(runningQueries: Seq[RunningQuery], runningQueryCount: Int)