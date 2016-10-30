package com.criteo.qwebmon

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

import scala.util.Random

class QwebmonController extends Controller {

  get("/running-queries/:target") { r: Request =>
    def randInt = Random.nextInt(100)
    Seq(
      RunningQuery("a.souletdebrugiere", randInt, "select 1"),
      RunningQuery("j.coffey", 350, "select distinct 1"),
      RunningQuery("a.pons", randInt, "select a + b"),
      RunningQuery("f.bar", randInt, "select foobar from foo join bar where baz = 344400 and bim between 12000 and 400000 group by foobar"),
      RunningQuery("f.jehl", randInt, "select 1")
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
