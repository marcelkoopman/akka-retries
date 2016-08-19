package flow

import akka.actor.{Actor, ActorLogging, Props}
import com.github.marcelkoopman.actorflow.flow.Orchestrator
import com.github.marcelkoopman.actorflow.flow.Orchestrator.{FinishedWorkEvt, WorkMsg}

import scala.concurrent.Future
import scala.util.{Failure, Random, Success}

/**
  * Created by marcel on 14-8-2016.
  */
object ServiceActor {
  def props:Props = Props(new ServiceActor())
}

private class ServiceActor extends Actor with ActorLogging {

  import context.dispatcher


  def receive = {
    case msg:WorkMsg => {
      log.info("I am "+this +" got msg " + msg.str)

      val f = Future {
        throw new NullPointerException("BOOM")
      }.onFailure {
        case e:NullPointerException => self ! e
      }

      sender ! "OK"
    }
    case ex:Exception => throw ex
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.info("Restarting because of "+reason.getLocalizedMessage)
    super.preRestart(reason, message)
  }
}
