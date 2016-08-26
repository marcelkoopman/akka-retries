package com.github.marcelkoopman.akka.retries

import akka.actor.ActorSystem
import akka.retries.orchestrator.Orchestrator
import akka.retries.orchestrator.Orchestrator.StartUpMsg
import com.typesafe.config.ConfigFactory

/**
  * Created by marcel on 14-8-2016.
  */
object AkkaRetriesApp extends App {

  override def main(args: Array[String]): Unit = {

    val config = ConfigFactory.load()
    val system = ActorSystem("akkaRetriesSystem", config.getConfig("akkaRetriesSystem"))

    val orchestrator = system.actorOf(Orchestrator.props, "orchestrator")
    orchestrator ! StartUpMsg("go")
  }

}
