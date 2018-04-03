package org.uaparser.scala

import MatcherOps._
import java.util.regex.{ Matcher, Pattern }

case class OS(family: String, major: Option[String] = None, minor: Option[String] = None, patch: Option[String] = None,
              patchMinor: Option[String] = None)

object OS {
  private[scala] def fromMap(m: Map[String, String]) = m.get("family").map { family =>
    OS(family, m.get("major"), m.get("minor"), m.get("patch"), m.get("patch_minor"))
  }

  private[this] val quotedBack1: Pattern = Pattern.compile(s"(${Pattern.quote("$1")})")

  private[this] def replacementBack1(matcher: Matcher)(replacement: String): String =
    if (matcher.groupCount() >= 1) {
      quotedBack1.matcher(replacement).replaceAll(matcher.group(1))
    } else replacement

  private[scala] case class OSPattern(
    pattern: Pattern,
    osReplacement: Option[String],
    v1Replacement: Option[String],
    v2Replacement: Option[String],
    v3Replacement: Option[String],
    v4Replacement: Option[String]
  ) {
    def process(agent: String): Option[OS] = {
      val matcher = pattern.matcher(agent)
      if (!matcher.find()) None else {
        osReplacement
          .map(replacementBack1(matcher))
          .orElse(matcher.groupAt(1)).map { family =>
            val major = v1Replacement
              .map(replacementBack1(matcher))
              .orElse(matcher.groupAt(2))
            val minor = v2Replacement.orElse(matcher.groupAt(3))
            val patch = v3Replacement.orElse(matcher.groupAt(4))
            val patchMinor = v4Replacement.orElse(matcher.groupAt(5))
            OS(family, major, minor, patch, patchMinor)
        }
      }
    }
  }

  private object OSPattern {
    def fromMap(m: Map[String, String]): Option[OSPattern] = m.get("regex").map { r =>
      OSPattern(Pattern.compile(r), m.get("os_replacement"), m.get("os_v1_replacement"), m.get("os_v2_replacement"),
        m.get("os_v3_replacement"), m.get("os_v4_replacement"))
    }
  }

  case class OSParser(patterns: List[OSPattern]) {
    def parse(agent: String): OS = patterns.foldLeft[Option[OS]](None) {
      case (None, pattern) => pattern.process(agent)
      case (result, _) => result
    }.getOrElse(OS("Other"))
  }

  object OSParser {
    def fromList(config: List[Map[String, String]]): OSParser = OSParser(config.map(OSPattern.fromMap).flatten)
  }
}
