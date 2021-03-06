package com.criteo.qwebmon

import com.criteo.qwebmon.drivers.FakeDbDriver
import com.twitter.finagle.http.Status._
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest

class QwebmonControllerTest extends FeatureTest {

  override val server = new EmbeddedHttpServer(new FinatraServer {
    override def dbDrivers: Map[String, DbDriver] = Map("fake-db" -> new FakeDbDriver("fake-db"))
  })

  "Qwebmon" should {
    "Provide a refresh for fake-db" in {
      val response = server.httpGet(
        path = "/refresh/fake-db",
        andExpect = Ok
      ).contentString

      response should startWith(
        """{"running_queries":[{"user":"johndoe","run_seconds":350,"query":"select distinct 1","hostname":"127.0.0.1"},"""
      )

      response should include(""""running_query_count":5""")
    }
  }

}
