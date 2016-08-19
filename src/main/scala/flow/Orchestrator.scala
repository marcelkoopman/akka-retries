package com.github.marcelkoopman.actorflow.flow


import akka.actor.SupervisorStrategy._
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props}
import akka.routing.FromConfig
import flow.ServiceActor
import com.github.marcelkoopman.actorflow.flow.Orchestrator.{FinishedWorkEvt, StartUpMsg, WorkMsg}
import scala.concurrent.duration._
/**
  * Created by marcel on 14-8-2016.
  */
object Orchestrator {
  def props: Props = Props(new Orchestrator())

  case class StartUpMsg(str: String)
  case class WorkMsg(str: String)

  case class FinishedWorkEvt(str: String)

}

private class Orchestrator extends Actor with ActorLogging {

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 2, withinTimeRange = 1 minute) {
      case _: ArithmeticException      => Resume
      case _: NullPointerException     => Restart
      case _: IllegalArgumentException => Stop
      case _: Exception                => Escalate
    }

  val router1: ActorRef =
    context.actorOf(FromConfig.props(ServiceActor.props), "router1")

  val router2: ActorRef =
    context.actorOf(FromConfig.props(ServiceActor.props), "router2")

  val router3: ActorRef =
    context.actorOf(FromConfig.props(ServiceActor.props), "router3")

  def receive = {

    case msg: StartUpMsg => {

      for ( a <- 1 to 4) {
        router3 ! WorkMsg(s"msg$a")
      }
    }

    case finished: FinishedWorkEvt => {
      log.info("Finished: " + finished + " from " + sender().path)
    }
  }
}
