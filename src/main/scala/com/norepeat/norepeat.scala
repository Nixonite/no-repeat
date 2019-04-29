package com.norepeat

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.util.Timeout

import scala.concurrent.{Await, duration, Future}
import scala.concurrent.duration._
import akka.pattern.ask
import bloomfilter.mutable.BloomFilter


final case class Message(val msg: String)
final case class CheckMessage(val msg: String)

class BloomFilterActor extends Actor {

  val expectedMessages = 100000
  val falsePositiveRate = 0.00001
  val bf = BloomFilter[String](expectedMessages, falsePositiveRate)

  override def receive: Receive = {
    case Message(msg) => bf.add(msg)
    case CheckMessage(msg) => sender() ! bf.mightContain(msg)
  }
}

object NoRepeat extends App {

  val system = ActorSystem("Chats")
  // default Actor constructor

  val bloomActor = system.actorOf(Props[BloomFilterActor], name="bloomactor")

  implicit val timeout = Timeout(30 seconds)
  bloomActor ! Message("hi")
  bloomActor ! Message("hi")
  val future: Future[Boolean] = ask(bloomActor, CheckMessage("hi")).mapTo[Boolean]
  val res = Await.result(future, timeout.duration)
  println(res)
  system.terminate()
}