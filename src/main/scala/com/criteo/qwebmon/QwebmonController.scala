package com.criteo.qwebmon

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

class QwebmonController extends Controller {

  get("/running-queries/:target") { r: Request =>
    Seq(
      RunningQuery("foo", 2, "select 1"),
      RunningQuery("foo", 350, "select distinct 1"),
      RunningQuery("foo", 19, "select a + b"),
      RunningQuery("foo", 40, "select foobar from foo join bar"),
      RunningQuery("foo", 1, "select 1")
    ).sortWith { case (a, b) => a.runSeconds > b.runSeconds }
  }

  get("/system-status/:target") { r: Request =>
    SystemStatus(
      runningQueryCount = 5,
      averageQueries = 4.5f,
      averageQueriesUnit = "minute"
    )
  }

  get("/") { r: Request =>
    response.ok.file("/index.html")
  }
}

case class RunningQuery(user: String, runSeconds: Int, query: String)

case class SystemStatus(runningQueryCount: Int, averageQueries: Float, averageQueriesUnit: String)
