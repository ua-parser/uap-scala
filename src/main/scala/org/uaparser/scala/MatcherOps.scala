package org.uaparser.scala

import java.util.regex.Matcher

object MatcherOps {
  implicit class MatcherImprovements(val m: Matcher) {
    import scala.util.control.Exception._
    def groupAt(i: Int) = catching(classOf[IndexOutOfBoundsException]).opt(Option(m.group(i))).flatten
  }
}
