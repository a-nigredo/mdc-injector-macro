package dev

package object nigredo {

  sealed trait Level

  case object Info extends Level

  case object Debug extends Level

  case object Error extends Level

  case object Trace extends Level

  case object Warn extends Level

}
