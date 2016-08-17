package com.github.marcelkoopman.actorflow

import akka.actor.ActorSystem
import com.github.marcelkoopman.actorflow.flow.Orchestrator
import com.github.marcelkoopman.actorflow.flow.Orchestrator.StartUpMsg

/**
  * Created by marcel on 14-8-2016.
  */
object ActorFlowApp extends App{

  override def main(args:Array[String]):Unit = {
    val system = ActorSystem("actorFlowSystem")
    val actorRef = system.actorOf(Orchestrator.props)
    actorRef ! StartUpMsg("go")
  }

}
