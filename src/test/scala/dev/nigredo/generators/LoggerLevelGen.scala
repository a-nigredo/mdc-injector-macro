package dev.nigredo.generators

import dev.nigredo._
import org.scalacheck.Gen

/**
  * Generate logs level
  */
object LoggerLevelGen extends (() => Gen[Level]) {
  def apply(): Gen[Level] = Gen.oneOf(Debug, Info, Trace, Error, Warn)
}
