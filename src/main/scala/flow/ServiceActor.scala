package com.github.marcelkoopman.actorflow

import akka.actor.{Actor, ActorLogging, Props}
import com.github.marcelkoopman.actorflo.SlowResource
import com.github.marcelkoopman.actorflow.flow.Orchestrator.{FailedWork, FinishedWorkEvt, WorkMsg}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by marcel on 14-8-2016.
  */
object ServiceActor {
  def props: Props = Props(new ServiceActor())
}

private class ServiceActor extends Actor with ActorLogging {

  def receive = {
    case msg: WorkMsg => {
      val theSender = sender
      val result = SlowResource.doSomeThingSlow(msg.str)
      result.onSuccess {
        case s => {
          theSender ! FinishedWorkEvt(s)
        }
      }

      result.onFailure {
        case f => {
          theSender ! FailedWork(f, msg)
        }
      }
    }
  }
}
