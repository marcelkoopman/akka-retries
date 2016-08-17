package com.github.marcelkoopman.actorflow.flow


import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.routing.FromConfig
import flow.ServiceActor
import com.github.marcelkoopman.actorflow.flow.Orchestrator.{FinishedWorkEvt, StartUpMsg, WorkMsg}

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

  val router1: ActorRef =
    context.actorOf(FromConfig.props(ServiceActor.props), "router1")

  val router2: ActorRef =
    context.actorOf(FromConfig.props(ServiceActor.props), "router2")

  def receive = {

    case msg: StartUpMsg => {

      for ( a <- 1 to 300) {
        router2 ! WorkMsg(s"msg$a")
      }
    }

    case finished: FinishedWorkEvt => {
      log.info("Finished: " + finished + " from " + sender().path)
    }
  }
}
