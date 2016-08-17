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

  //private val serviceActor = context.actorOf(FromConfig.props(ServiceActor.props), "ServiceActor")
  val router1: ActorRef =
    context.actorOf(FromConfig.props(ServiceActor.props), "router1")

  def receive = {

    case msg: StartUpMsg => {

      for ( a <- 1 to 3) {
        router1 ! WorkMsg("msg")
      }
    }

    case finished: FinishedWorkEvt => {
      log.info("Finished: " + finished + " from " + sender().path)
    }
  }
}
