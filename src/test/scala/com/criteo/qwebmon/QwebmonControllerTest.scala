package com.criteo.qwebmon

import com.criteo.qwebmon.drivers.FakeDbDriver
import com.twitter.finagle.http.Status._
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest

class QwebmonControllerTest extends FeatureTest {

  override val server = new EmbeddedHttpServer(new FinatraServer {
    override def dbDrivers: Map[String, DbDriver] = Map("fake-db" -> new FakeDbDriver)
  })

  "Qwebmon" should {
    "Provide a refresh for fake-db" in {
      val response = server.httpGet(
        path = "/refresh/fake-db",
        andExpect = Ok
      ).contentString
      response should startWith(
        """{"running_queries":[{"user":"johndoe","run_seconds":350,"query":"select distinct 1"},"""
      )
      response should include(
        """"system_status":{"running_query_count":5,"average_queries":4.5,"average_queries_unit":"minute"}"""
      )
    }
  }

}
