package com.github.marcelkoopman.actorflo

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

/**
  * Created by marcel on 26-8-2016.
  */
object SlowResource {

  def doSomeThingSlow(str:String):Future[String] = {

    Future {
      if (Random.nextBoolean()) {
        Thread.sleep(1400)
        throw new NullPointerException("whoops")
      }
      str.reverse
    }
  }
}
