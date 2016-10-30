package com.criteo.qwebmon.drivers

import com.criteo.qwebmon.{RunningQuery, SystemStatus, DbStatus, DbDriver}

import scala.util.Random

class FakeDbDriver extends DbDriver {

  private def randInt = Random.nextInt(100)

  override def latestStatus: DbStatus = DbStatus(
    runningQueries = Seq(
      RunningQuery("alexanderhamilton", randInt, "select 1"),
      RunningQuery("johndoe", 350, "select distinct 1"),
      RunningQuery("asmith", randInt, "select a + b"),
      RunningQuery("foobar", randInt, "select foobar from foo join bar where baz = 344400 and bim between 12000 and 400000 group by foobar"),
      RunningQuery("janedoe", randInt, "select 1")
    ).sortWith { case (a, b) => a.runSeconds > b.runSeconds },
    systemStatus = SystemStatus(
      runningQueryCount = 5,
      averageQueries = 4.5f,
      averageQueriesUnit = "minute"
    )
  )

}
