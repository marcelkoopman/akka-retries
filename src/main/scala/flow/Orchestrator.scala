package com.github.marcelkoopman.actorflow.flow


import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.routing.FromConfig
import com.github.marcelkoopman.actorflow.ServiceActor
import com.github.marcelkoopman.actorflow.flow.Orchestrator._

/**
  * Created by marcel on 14-8-2016.
  */
object Orchestrator {
  def props: Props = Props(new Orchestrator())

  case class RetryConfig(retryCount: Int, sleepTime: Long)

  case class StartUpMsg(str: String)

  case class WorkMsg(str: String, retryConfig: RetryConfig)

  case class FinishedWork(str: String)

  case class FailedWork(failure: Throwable, original: WorkMsg)

}

private class Orchestrator extends Actor with ActorLogging {

  val router2: ActorRef =
    context.actorOf(FromConfig.props(ServiceActor.props), "router2")

  val totalWorkDone = new AtomicInteger
  val totalWork = 5

  def receive = {
    case msg: StartUpMsg => {
      for (a <- 1 to totalWork) {
        router2 ! WorkMsg(s"msg$a", RetryConfig(5, 1000))
      }
    }
    case finished: FinishedWork => {
      log.info("Finished: " + finished + " from " + sender().path)
      val workDone = totalWorkDone.incrementAndGet()
      log.info("Total done {}", workDone)
      if (workDone == totalWork) {
        log.info("I've done my work, nothing to do here!")
      }
    }
    case failed: FailedWork => {

      val retryConfig = failed.original.retryConfig
      val retryCount = retryConfig.retryCount
      if (retryCount == 0) {
        log.info("Retrying {} for the last time", failed.original.str)
        Thread.sleep(retryConfig.sleepTime)
        sender ! failed.original
      } else if (retryCount >= 0) {
        log.info("Retrying {} retries remaining: {}", failed.original.str, retryCount)
        Thread.sleep(retryConfig.sleepTime)
        sender ! failed.original
      }
      else {
        log.info("No more retries left for {}", failed.original.str)
        log.error("Finally failed: {} cause: {}", failed.original.str, failed.failure.getLocalizedMessage)
      }
    }
  }
}
