package com.criteo.qwebmon

import com.twitter.finagle.http.Status._
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest

class QwebmonControllerTest extends FeatureTest {

  override val server = new EmbeddedHttpServer(new FinatraServer)

  "Qwebmon" should {
    "Provide a refresh for fake-db" in {
      val response = server.httpGet(
        path = "/refresh/fake-db",
        andExpect = Ok
      ).contentString
      response should startWith(
        """{"running_queries":[{"user":"j.coffey","run_seconds":350,"query":"select distinct 1"},"""
      )
    }
  }

}
