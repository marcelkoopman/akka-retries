package akka.retries.services

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.CircuitBreaker
import akka.retries.orchestrator.Orchestrator._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by marcel on 14-8-2016.
  */
object ServiceActor {
  def props: Props = Props(new ServiceActor())
}

private class ServiceActor extends Actor with ActorLogging {

  import scala.concurrent.duration._

  val breaker =
    new CircuitBreaker(context.system.scheduler,
      maxFailures = 10,
      callTimeout = 1 seconds,
      resetTimeout = 1 seconds).
      onOpen(println("SYSTEM FAILURE: circuit breaker opened!")).
      onClose(println("SYSTEM RECOVER: circuit breaker closed!")).
      onHalfOpen(println("SYSTEM FAILURE: circuit breaker half-open"))


  def receive = {
    case msg: WorkMsg => {
      for (child <- context.children) {
        log.info("I have child {}", child.path)
      }
      val theSender = sender()
      val result = breaker.withCircuitBreaker((UnreliableResource.getReversedString(msg.str)))
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
