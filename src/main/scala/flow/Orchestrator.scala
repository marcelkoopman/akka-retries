package com.github.marcelkoopman.actorflow.flow


import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.routing.FromConfig
import com.github.marcelkoopman.actorflow.ServiceActor
import com.github.marcelkoopman.actorflow.flow.Orchestrator.{FailedWork, FinishedWork, StartUpMsg, WorkMsg}

/**
  * Created by marcel on 14-8-2016.
  */
object Orchestrator {
  def props: Props = Props(new Orchestrator())

  case class StartUpMsg(str: String)

  case class WorkMsg(str: String, retryCount: Int)

  case class FinishedWork(str: String)

  case class FailedWork(failure: Throwable, original: WorkMsg)

}

private class Orchestrator extends Actor with ActorLogging {

  val router2: ActorRef =
    context.actorOf(FromConfig.props(ServiceActor.props), "router2")

  def receive = {
    case msg: StartUpMsg => {
      for (a <- 1 to 15) {
        router2 ! WorkMsg(s"msg$a", 2)
      }
    }
    case finished: FinishedWork => {
      log.info("Finished: " + finished + " from " + sender().path)
    }
    case failed: FailedWork => {

      val retryCount = failed.original.retryCount
      if (retryCount >= 0) {
        if (failed.original.retryCount == 0) {
          log.info("Retrying {} for the last time", failed.original.str)
        } else {
          log.info("Retrying {} retries remaining: {}", failed.original.str, failed.original.retryCount)
        }
        sender ! failed.original
      } else {
        log.info("No more retries left for {}", failed.original.str)
        log.error("Finally failed: {} cause: {}", failed.original.str, failed.failure.getLocalizedMessage)
      }
    }
  }
}
