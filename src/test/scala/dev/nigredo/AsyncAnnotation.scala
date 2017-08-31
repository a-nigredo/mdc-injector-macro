package dev.nigredo

import org.slf4j.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@MdcInjector()
class AsyncAnnotation(logger: Logger) {

  def log = Future {
    logger.info("msg")
  }

  def log2 = {
    Future {
      logger.info("msg")
    }
  }

  def log3 = {
    val run: Unit => Future[Unit] = _ => Future {
      logger.info("msg")
    }
    run()
  }
}
