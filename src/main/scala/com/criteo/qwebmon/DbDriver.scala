package com.criteo.qwebmon

trait DbDriver {

  def name: String

  def runningQueries: Seq[RunningQuery]

}

case class RunningQuery(user: String, runSeconds: Int, query: String, hostname: String)