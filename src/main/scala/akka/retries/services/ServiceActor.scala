package akka.retries.services

import akka.actor.{Actor, ActorLogging, Props}
import akka.retries.orchestrator.Orchestrator._

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
          theSender ! FinishedWork(s)
        }
      }

      result.onFailure {
        case f => {
          val retryRemaining = msg.retryConfig.retryCount - 1
          theSender ! FailedWork(f, WorkMsg(msg.str, RetryConfig(retryRemaining, msg.retryConfig.sleepSeconds)))
        }
      }
    }
  }
}
