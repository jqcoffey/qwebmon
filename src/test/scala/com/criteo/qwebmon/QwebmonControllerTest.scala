package com.criteo.qwebmon

import com.twitter.finagle.http.Status._
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest

class QwebmonControllerTest extends FeatureTest {

  override val server = new EmbeddedHttpServer(new FinatraServer)

  "Qwebmon" should {
    "Provide queries as a json array" in {
      val response = server.httpGet(
        path = "/running-queries/foo",
        andExpect = Ok
      ).contentString
      response should startWith(
        """[{"user":"foo","run_seconds":350,"query":"select distinct 1"},""".stripMargin
      )
    }
  }

}
