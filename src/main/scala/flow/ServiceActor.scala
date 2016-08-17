package flow

import akka.actor.{Actor, ActorLogging, Props}
import com.github.marcelkoopman.actorflow.flow.Orchestrator
import com.github.marcelkoopman.actorflow.flow.Orchestrator.{FinishedWorkEvt, WorkMsg}

/**
  * Created by marcel on 14-8-2016.
  */
object ServiceActor {
  def props:Props = Props(new ServiceActor())
}

private class ServiceActor extends Actor with ActorLogging {

  def receive = {
    case msg:WorkMsg => {
      log.info("I am "+this +" got msg " + msg.str)

      //throw new NullPointerException("BOOM")

      sender ! FinishedWorkEvt(msg.str)
    }
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.info("Restarting because of "+reason.getLocalizedMessage)
    super.preRestart(reason, message)
  }
}
