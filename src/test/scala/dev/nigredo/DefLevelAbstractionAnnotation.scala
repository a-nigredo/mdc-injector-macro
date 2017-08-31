package dev.nigredo

import org.slf4j.{Logger, MarkerFactory}

class DefLevelAbstractionAnnotationImpl(level: Level, logger: Logger) extends DefLevelAbstractionAnnotation {

  @MdcInjector()
  override def log(): Unit = level match {
    case Info =>
      logger.info("msg")
      logger.info("msg", new Exception("test"))
      logger.info(MarkerFactory.getMarker("test"), "msg")
      logger.info(MarkerFactory.getMarker("test"), "msg", new Exception("test"))
    case Debug =>
      logger.debug("msg")
      logger.debug("msg", new Exception("test"))
      logger.debug(MarkerFactory.getMarker("test"), "msg")
      logger.debug(MarkerFactory.getMarker("test"), "msg", new Exception("test"))
    case Warn =>
      logger.warn("msg")
      logger.warn("msg", new Exception("test"))
      logger.warn(MarkerFactory.getMarker("test"), "msg")
      logger.warn(MarkerFactory.getMarker("test"), "msg", new Exception("test"))
    case Error =>
      logger.error("msg")
      logger.error("msg", new Exception("test"))
      logger.error(MarkerFactory.getMarker("test"), "msg")
      logger.error(MarkerFactory.getMarker("test"), "msg", new Exception("test"))
    case Trace =>
      logger.trace("msg")
      logger.trace("msg", new Exception("test"))
      logger.trace(MarkerFactory.getMarker("test"), "msg")
      logger.trace(MarkerFactory.getMarker("test"), "msg", new Exception("test"))
  }

  @MdcInjector()
  override def log2: Unit = level match {
    case Info =>
      logger.info("msg")
      logger.info("msg", new Exception("test"))
      logger.info(MarkerFactory.getMarker("test"), "msg")
      logger.info(MarkerFactory.getMarker("test"), "msg", new Exception("test"))
    case Debug =>
      logger.debug("msg")
      logger.debug("msg", new Exception("test"))
      logger.debug(MarkerFactory.getMarker("test"), "msg")
      logger.debug(MarkerFactory.getMarker("test"), "msg", new Exception("test"))
    case Warn =>
      logger.warn("msg")
      logger.warn("msg", new Exception("test"))
      logger.warn(MarkerFactory.getMarker("test"), "msg")
      logger.warn(MarkerFactory.getMarker("test"), "msg", new Exception("test"))
    case Error =>
      logger.error("msg")
      logger.error("msg", new Exception("test"))
      logger.error(MarkerFactory.getMarker("test"), "msg")
      logger.error(MarkerFactory.getMarker("test"), "msg", new Exception("test"))
    case Trace =>
      logger.trace("msg")
      logger.trace("msg", new Exception("test"))
      logger.trace(MarkerFactory.getMarker("test"), "msg")
      logger.trace(MarkerFactory.getMarker("test"), "msg", new Exception("test"))
  }

  @MdcInjector()
  override def log3(i: Int): Unit = level match {
    case Info =>
      logger.info("msg")
      logger.info("msg", new Exception("test"))
      logger.info(MarkerFactory.getMarker("test"), "msg")
      logger.info(MarkerFactory.getMarker("test"), "msg", new Exception("test"))
    case Debug =>
      logger.debug("msg")
      logger.debug("msg", new Exception("test"))
      logger.debug(MarkerFactory.getMarker("test"), "msg")
      logger.debug(MarkerFactory.getMarker("test"), "msg", new Exception("test"))
    case Warn =>
      logger.warn("msg")
      logger.warn("msg", new Exception("test"))
      logger.warn(MarkerFactory.getMarker("test"), "msg")
      logger.warn(MarkerFactory.getMarker("test"), "msg", new Exception("test"))
    case Error =>
      logger.error("msg")
      logger.error("msg", new Exception("test"))
      logger.error(MarkerFactory.getMarker("test"), "msg")
      logger.error(MarkerFactory.getMarker("test"), "msg", new Exception("test"))
    case Trace =>
      logger.trace("msg")
      logger.trace("msg", new Exception("test"))
      logger.trace(MarkerFactory.getMarker("test"), "msg")
      logger.trace(MarkerFactory.getMarker("test"), "msg", new Exception("test"))
  }

  @MdcInjector()
  override def log4(i: Int)(implicit ev: String): Unit = level match {
    case Info =>
      logger.info("msg")
      logger.info("msg", new Exception("test"))
      logger.info(MarkerFactory.getMarker("test"), "msg")
      logger.info(MarkerFactory.getMarker("test"), "msg", new Exception("test"))
    case Debug =>
      logger.debug("msg")
      logger.debug("msg", new Exception("test"))
      logger.debug(MarkerFactory.getMarker("test"), "msg")
      logger.debug(MarkerFactory.getMarker("test"), "msg", new Exception("test"))
    case Warn =>
      logger.warn("msg")
      logger.warn("msg", new Exception("test"))
      logger.warn(MarkerFactory.getMarker("test"), "msg")
      logger.warn(MarkerFactory.getMarker("test"), "msg", new Exception("test"))
    case Error =>
      logger.error("msg")
      logger.error("msg", new Exception("test"))
      logger.error(MarkerFactory.getMarker("test"), "msg")
      logger.error(MarkerFactory.getMarker("test"), "msg", new Exception("test"))
    case Trace =>
      logger.trace("msg")
      logger.trace("msg", new Exception("test"))
      logger.trace(MarkerFactory.getMarker("test"), "msg")
      logger.trace(MarkerFactory.getMarker("test"), "msg", new Exception("test"))
  }

  @MdcInjector()
  override def log5(implicit ev: String): Unit = level match {
    case Info =>
      logger.info("msg")
      logger.info("msg", new Exception("test"))
      logger.info(MarkerFactory.getMarker("test"), "msg")
      logger.info(MarkerFactory.getMarker("test"), "msg", new Exception("test"))
    case Debug =>
      logger.debug("msg")
      logger.debug("msg", new Exception("test"))
      logger.debug(MarkerFactory.getMarker("test"), "msg")
      logger.debug(MarkerFactory.getMarker("test"), "msg", new Exception("test"))
    case Warn =>
      logger.warn("msg")
      logger.warn("msg", new Exception("test"))
      logger.warn(MarkerFactory.getMarker("test"), "msg")
      logger.warn(MarkerFactory.getMarker("test"), "msg", new Exception("test"))
    case Error =>
      logger.error("msg")
      logger.error("msg", new Exception("test"))
      logger.error(MarkerFactory.getMarker("test"), "msg")
      logger.error(MarkerFactory.getMarker("test"), "msg", new Exception("test"))
    case Trace =>
      logger.trace("msg")
      logger.trace("msg", new Exception("test"))
      logger.trace(MarkerFactory.getMarker("test"), "msg")
      logger.trace(MarkerFactory.getMarker("test"), "msg", new Exception("test"))
  }
}

abstract class DefLevelAbstractionAnnotation {

  @MdcInjector()
  def log(): Unit

  @MdcInjector()
  def log2: Unit

  @MdcInjector()
  def log3(i: Int): Unit

  @MdcInjector()
  def log4(i: Int)(implicit ev: String): Unit

  @MdcInjector()
  def log5(implicit ev: String): Unit
}
