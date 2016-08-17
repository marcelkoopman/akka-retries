package com.github.marcelkoopman.actorflow

import akka.actor.ActorSystem
import com.github.marcelkoopman.actorflow.flow.Orchestrator
import com.github.marcelkoopman.actorflow.flow.Orchestrator.StartUpMsg
import com.typesafe.config.ConfigFactory

/**
  * Created by marcel on 14-8-2016.
  */
object ActorFlowApp extends App{

  override def main(args:Array[String]):Unit = {
    val config = ConfigFactory.load()
    val system = ActorSystem("actorFlowSystem", config.getConfig("actorFlowSystem"))
    val actorRef = system.actorOf(Orchestrator.props, "orchestrator")
    actorRef ! StartUpMsg("go")

  }

}
