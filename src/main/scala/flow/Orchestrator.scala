package com.github.marcelkoopman.actorflow.flow

import akka.actor.{Actor, ActorLogging, Props}
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

  private val serviceActorA = context.actorOf(ServiceActor.props)
  private val serviceActorB = context.actorOf(ServiceActor.props)
  private val serviceActorC = context.actorOf(ServiceActor.props)

  def receive = {

    case msg: StartUpMsg => {

      for ( a <- 1 to 3) {
        serviceActorA ! WorkMsg("msg")
      }
    }

    case finished: FinishedWorkEvt => {
      log.info("Finished: " + finished + " from " + sender().path)
    }
  }
}
