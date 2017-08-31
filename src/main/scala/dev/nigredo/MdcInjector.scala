package dev.nigredo

import scala.annotation.StaticAnnotation
import scala.collection.immutable._
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

/**
  * Macro copy variables between MDC context if there is explicit logger call in def
  *
  * Useful in async programming
  *
  * Transformation example:
  *
  * {{{
  *   @MdcInjector()
  *   class A {
  *       def logMe = logger.info("log me msg")
  *     }
  * }}}
  *
  *
  * Will be:
  *
  * {{{
  *   @MdcInjector()
  *   class A {
  *       def logMe = {
  *           val mdcCopy = org.slf4j.MDC.getCopyOfContextMap
  *           {
  *              org.slf4j.MDC.setContextMap(mdcCopy)
  *             logger.debug("msg")
  *           }
  *        }
  *      }
  * }}}
  *
  * Async transformation:
  *
  * {{{
  *   @MdcInjector()
  *   class A {
  *       def logMe = Future(logger.info("log me msg"))
  *     }
  * }}}
  *
  * Will be:
  *
  * {{{
  *   @MdcInjector()
  *   class A {
  *       def logMe = {
  *           val mdcCopy = org.slf4j.MDC.getCopyOfContextMap
  *           Future {
  *             org.slf4j.MDC.setContextMap(mdcCopy)
  *             logger.debug("msg")
  *           }
  *        }
  *      }
  * }}
  *
  * @param loggerField name of a logger field which is used for searching logger calls
  * */
class MdcInjector(loggerField: String = "logger") extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro MdcInjectorImpl.impl
}

object MdcInjectorImpl {

  def impl(c: blackbox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    val (loggerField, annot) = {

      val annotationName = classOf[MdcInjector].getSimpleName

      def toValue[T](tree: Tree): T = c.eval[T](c.Expr(tree))

      def toLetterAndDigitStr(tree: Tree) = (toValue[String] _ andThen onlyLetterAndDigit) (tree)

      def onlyLetterAndDigit(str: String): String = str.filter(_.isLetterOrDigit)

      c.prefix.tree.duplicate match {
        case q"new $annot(loggerField = $loggerName)" => (toLetterAndDigitStr(loggerName.asInstanceOf[Tree]), annotationName)
        case q"new $annot($loggerName)" => (toLetterAndDigitStr(loggerName.asInstanceOf[Tree]), annotationName)
        case q"new $annot()" => ("logger", annotationName)
        case _ => c.abort(c.enclosingPosition, "unexpected annotation parameters! Correct order: loggerField?")
      }
    }

    def rewriteDef(defn: DefDef) = {

      def isLoggerCall(tree: Tree): Boolean =
        tree.exists {
          case Ident(TermName(tname)) => tname == loggerField
          case _ => false
        }

      val mdcCopyValName = TermName(c.freshName("mdcCopy"))

      val transformer = new Transformer {

        private val rewriteLogCallBlock: Tree => Tree = tree => {
          q"""
           if($mdcCopyValName.isDefined) {
              org.slf4j.MDC.setContextMap($mdcCopyValName.get)
           }
           $tree
         """
        }

        override def transform(tree: c.universe.Tree): c.universe.Tree = {
          super.transform(tree) match {
            case c@q"$logField.$method($str)" if isLoggerCall(logField) => rewriteLogCallBlock(c)
            case c@q"$logField.$method($str, $cause)" if isLoggerCall(logField) => rewriteLogCallBlock(c)
            case c@q"$logField.$method($str, ..$any)" if isLoggerCall(logField) => rewriteLogCallBlock(c)
            case c@q"$logField.$method($marker, $str)" if isLoggerCall(logField) => rewriteLogCallBlock(c)
            case c@q"$logField.$method($marker, $str, $cause)" if isLoggerCall(logField) => rewriteLogCallBlock(c)
            case c@q"$logField.$method($marker, $str, ..$any)" if isLoggerCall(logField) => rewriteLogCallBlock(c)
            case v@_ => v
          }
        }
      }

      if (defn.rhs.nonEmpty) {
        object FoundLoggerCall extends Traverser {
          var isFound = false

          override def traverse(tree: c.universe.Tree): Unit = {
            tree match {
              case q"$logField.$method($str)" if isLoggerCall(logField) => isFound = true
              case q"$logField.$method($str, $cause)" if isLoggerCall(logField) => isFound = true
              case q"$logField.$method($str, ..$any)" if isLoggerCall(logField) => isFound = true
              case q"$logField.$method($marker, $str)" if isLoggerCall(logField) => isFound = true
              case q"$logField.$method($marker, $str, $cause)" if isLoggerCall(logField) => isFound = true
              case q"$logField.$method($marker, $str, ..$any)" if isLoggerCall(logField) => isFound = true
              case v@_ => super.traverse(v)
            }
          }
        }
        FoundLoggerCall.traverse(defn.rhs)
        if (FoundLoggerCall.isFound) {
          val nBody =
            q"""
               val $mdcCopyValName = Option(org.slf4j.MDC.getCopyOfContextMap())
               ${defn.rhs}
             """
          DefDef(defn.mods, defn.name, defn.tparams, defn.vparamss, defn.tpt, transformer.transform(nBody))
        } else defn
      }
      else defn
    }

    def isNotAnnotated(defn: DefDef) = !defn.mods.annotations.exists {
      case q"new $annot($_)" => true
      case _ => false
    }

    val result = annottees.map(_.tree).collect {
      case defn: DefDef => rewriteDef(defn)
      case q"$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends { ..$earlydefns } with ..$parents { $self => ..$stats }" =>
        val newStats = stats.map {
          case defn: DefDef if isNotAnnotated(defn) => rewriteDef(defn)
          case v@_ => v
        }
        q"$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends { ..$earlydefns } with ..$parents { $self => ..$newStats }"
      case q"$mods trait $tpname[..$tparams] extends { ..$earlydefns } with ..$parents { $self => ..$stats }" =>
        val newStats = stats.map {
          case defn: DefDef if isNotAnnotated(defn) => rewriteDef(defn)
          case v@_ => v
        }
        q"$mods trait $tpname[..$tparams] extends { ..$earlydefns } with ..$parents { $self => ..$newStats }"
      case q"$mods object $tname extends { ..$earlydefns } with ..$parents { $self => ..$body }" =>
        val newStats = body.map {
          case defn: DefDef if isNotAnnotated(defn) => rewriteDef(defn)
          case v@_ => v
        }
        q"$mods object $tname extends { ..$earlydefns } with ..$parents { $self => ..$newStats }"
      case _ => c.abort(c.enclosingPosition, "Incorrect usage of @MdcInjector. Can be applied to Class, Trait, Object, def")
    }

    c.Expr[Any](q"..$result")
  }
}