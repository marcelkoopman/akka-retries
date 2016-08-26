package com.github.marcelkoopman.akka.retries.orchestrator


import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.routing.FromConfig
import com.github.marcelkoopman.akka.retries.orchestrator.Orchestrator._
import com.github.marcelkoopman.akka.retries.services.ServiceActor

/**
  * Created by marcel on 14-8-2016.
  */
object Orchestrator {
  def props: Props = Props(new Orchestrator())
  case class RetryConfig(retryCount: Int, sleepSeconds: Int)
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
        router2 ! WorkMsg(s"msg$a", RetryConfig(5, 2))
      }
    }
    case finished: FinishedWork => {
      log.info("Finished: " + finished + " from " + sender().path)
      val workDone = totalWorkDone.incrementAndGet()
      log.info("Total done: {}", workDone)
      if (workDone == totalWork) {
        log.info("I've done all my work, nothing to do here!")
      }
    }
    case failed: FailedWork => {
      val retryConfig = failed.original.retryConfig
      val retryCount = retryConfig.retryCount
      if (retryCount == 0) {
        log.info("Retrying {} for the last time", failed.original.str)
        retry(failed, retryConfig)
      } else if (retryCount >= 0) {
        log.info("Retrying {} retries remaining: {}", failed.original.str, retryCount)
        retry(failed, retryConfig)
      }
      else {
        log.info("No more retries left for {}", failed.original.str)
        log.error("Finally failed: {} cause: {}", failed.original.str, failed.failure.getLocalizedMessage)
      }
    }
  }

  def retry(failed: FailedWork, retryConfig: RetryConfig): Unit = {
    log.info("Wait for {} second(s)...", retryConfig.sleepSeconds)
    TimeUnit.SECONDS.sleep(retryConfig.sleepSeconds)
    sender ! failed.original
  }
}
