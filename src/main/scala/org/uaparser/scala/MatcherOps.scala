package org.uaparser.scala

import java.util.regex.Matcher

object MatcherOps {
  implicit class MatcherImprovements(val m: Matcher) extends AnyVal {
    // Tries to safely return the matching group at index i wrapped in an Option.
    // We also take care of converting empty strings to a None, because it seems possible in uap-core to define matching
    // groups that capture empty strings. At the time, the semantics of None and empty strings seemed to match.
    def groupAt(i: Int): Option[String] = {
      try {
        val matched = m.group(i)
        if (matched == null || matched.isEmpty) None
        else Some(matched)
      } catch {
        case _: IndexOutOfBoundsException => None
      }
    }
  }
}
