package com.criteo.qwebmon

trait DbDriver {

  def latestStatus: DbStatus

}

case class DbStatus(runningQueries: Seq[RunningQuery], systemStatus: SystemStatus)

case class RunningQuery(user: String, runSeconds: Int, query: String)

case class SystemStatus(runningQueryCount: Int, averageQueries: Float, averageQueriesUnit: String)