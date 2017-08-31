package dev.nigredo

import org.slf4j.Logger

@MdcInjector()
class ValVarAnnotation(logger: Logger) {

  def log = {
    val r = logger.info("msg")
    r
  }

  def log2 = {
    var r = logger.info("msg")
    r
  }
}
