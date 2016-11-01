package com.criteo.qwebmon

trait DbDriver {

  def targetName: String

  def latestStatus: DbStatus

}

case class DbStatus(runningQueries: Seq[RunningQuery], systemStatus: SystemStatus)

case class RunningQuery(user: String, runSeconds: Int, query: String, hostname: String)

case class SystemStatus(runningQueryCount: Int, averageQueries: Float, averageQueriesUnit: String)