package com.main

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.Http

object Main {

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("Main")
    implicit val materializer = ActorMaterializer()

    lazy val routes: Route = get { // `get` for HTTP GET method
      complete("Hello World")
    }

    Http().bindAndHandle(routes, "localhost", 8080)
  }

}
