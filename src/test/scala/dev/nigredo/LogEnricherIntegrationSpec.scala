package dev.nigredo

import java.io.{ByteArrayOutputStream, OutputStream}

import ch.qos.logback.classic
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.OutputStreamAppender
import dev.nigredo.LogEnricherIntegrationSpec._
import dev.nigredo.generators.LoggerLevelGen
import org.scalacheck.Arbitrary
import org.slf4j.{Logger, LoggerFactory, MDC}
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

class LogEnricherIntegrationSpec
  extends Specification
    with ScalaCheck {

  sequential

  implicit val mockStr: String = "mockstr"

  implicit val logArbitrary: Arbitrary[Level] = Arbitrary(LoggerLevelGen())

  "Annotation" should {
    def assertMsg(expected: String) = expected.startsWith("42 msg") mustEqual true

    "be applied to class without parent" in prop { level: Level =>
      val (appender, logger) = getLogger
      val instance = new TopLevelAnnotation(level, logger)

      instance.log()
      assertMsg(appender.toString)

      instance.log2
      assertMsg(appender.toString)

      instance.log3(1)
      assertMsg(appender.toString)

      instance.log4(1)
      assertMsg(appender.toString)

      instance.log5
      assertMsg(appender.toString)
    }
    "be applied when parent is annotated but child" in prop { level: Level =>
      val (appender, logger) = getLogger
      val instance = new ChildWithoutAnnotation(level, logger)
      instance.log()
      assertMsg(appender.toString)
      instance.log2
      assertMsg(appender.toString)
      instance.log3(1)
      assertMsg(appender.toString)
      instance.log4(1)
      assertMsg(appender.toString)
      instance.log5
      assertMsg(appender.toString)
    }
    "be applied only one time when parent and child are both annotated" in prop { level: Level =>
      val (appender, logger) = getLogger
      val instance = new ChildWithAnnnotation(level, logger)
      instance.log()
      assertMsg(appender.toString)
      instance.log2
      assertMsg(appender.toString)
      instance.log3(1)
      assertMsg(appender.toString)
      instance.log4(1)
      assertMsg(appender.toString)
      instance.log5
      assertMsg(appender.toString)
    }
    "be applied when parent's defs are annotated but child" in prop { level: Level =>
      val (appender, logger) = getLogger
      val instance = new ChildWithoutDefAnnotation(level, logger)
      instance.log()
      assertMsg(appender.toString)
      instance.log2
      assertMsg(appender.toString)
      instance.log3(1)
      assertMsg(appender.toString)
      instance.log4(1)
      assertMsg(appender.toString)
      instance.log5
      assertMsg(appender.toString)
    }
    "be applied only one time when parent and child defs are both annotated" in prop { level: Level =>
      val (appender, logger) = getLogger
      val instance = new ChildWithDefAnnotation(level, logger)
      instance.log()
      assertMsg(appender.toString)
      instance.log2
      assertMsg(appender.toString)
      instance.log3(1)
      assertMsg(appender.toString)
      instance.log4(1)
      assertMsg(appender.toString)
      instance.log5
      assertMsg(appender.toString)
    }
    "be applied to class defs and the class is without parent" in prop { level: Level =>
      val (appender, logger) = getLogger
      val instance = new DefLevelAnnotation(level, logger)
      instance.log()
      assertMsg(appender.toString)
      instance.log2
      assertMsg(appender.toString)
      instance.log3(1)
      assertMsg(appender.toString)
      instance.log4(1)
      assertMsg(appender.toString)
      instance.log5
      assertMsg(appender.toString)
    }
    "be applied when child and parent are annotated and parent is abstract" in prop { level: Level =>
      val (appender, logger) = getLogger
      val instance = new DefLevelAbstractionAnnotationImpl(level, logger)
      instance.log()
      assertMsg(appender.toString)
      instance.log2
      assertMsg(appender.toString)
      instance.log3(1)
      assertMsg(appender.toString)
      instance.log4(1)
      assertMsg(appender.toString)
      instance.log5
      assertMsg(appender.toString)
    }
    "be applied to future call" in {
      val (appender, logger) = getLogger
      val instance = new AsyncAnnotation(logger)
      Await.result(instance.log, Duration.Inf)
      assertMsg(appender.toString)
      Await.result(instance.log2, Duration.Inf)
      assertMsg(appender.toString)
      Await.result(instance.log3, Duration.Inf)
      assertMsg(appender.toString)
    }
    "be applied to var/val assignment" in {
      val (appender, logger) = getLogger
      val instance = new ValVarAnnotation(logger)
      instance.log
      assertMsg(appender.toString)
      instance.log2
      assertMsg(appender.toString)
    }
  }
}

object LogEnricherIntegrationSpec {
  def getLogger: (OutputStream, classic.Logger) = {
    MDC.put("enrichWith", "42")
    val out = new ByteArrayOutputStream
    val logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME).asInstanceOf[ch.qos.logback.classic.Logger]

    val encoder = new PatternLayoutEncoder()
    encoder.setContext(logger.getLoggerContext)
    encoder.setPattern("%X{enrichWith} %m")
    encoder.start()

    val appender = new OutputStreamAppender[ILoggingEvent]()
    appender.setImmediateFlush(true)
    appender.setContext(logger.getLoggerContext)
    appender.setEncoder(encoder)
    appender.setOutputStream(out)
    appender.start()

    logger.detachAndStopAllAppenders()
    logger.setAdditive(false)
    logger.setLevel(ch.qos.logback.classic.Level.TRACE)
    logger.addAppender(appender)
    (out, logger)
  }
}
