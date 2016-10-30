package com.criteo.qwebmon

import com.criteo.qwebmon.drivers.FakeDbDriver
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

class QwebmonController extends Controller {

  val drivers = Map(
    "fake-db" -> new FakeDbDriver
  )

  get("/refresh/:target") { r: Request =>
    val targetName = r.getParam("target")
    drivers.get(targetName).map(_.latestStatus).getOrElse(
      response.badRequest(s"invalid target supplied: $targetName")
    )
  }

  get("/") { r: Request =>
    response.ok.file("/index.html")
  }

}
