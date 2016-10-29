package com.criteo.qwebmon

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

class StaticController extends Controller {

  get("/:*") { request: Request =>
    response.ok.fileOrIndex(
      request.params("*"),
      "404.html"
    )
  }

}
