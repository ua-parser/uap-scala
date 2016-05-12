package ua.parser

import java.util.regex.Pattern
import MatcherOps._

case class OS(family: String, major: Option[String] = None, minor: Option[String] = None, patch: Option[String] = None,
  patchMinor: Option[String] = None)

object OS {
  private[parser] def fromMap(m: Map[String, String]) = m.get("family").map { family =>
    OS(family, m.get("major"), m.get("minor"), m.get("patch"), m.get("patch_minor"))
  }

  private[parser] case class OSPattern(pattern: Pattern, osReplacement: Option[String], v1Replacement: Option[String],
      v2Replacement: Option[String], v3Replacement: Option[String],
      v4Replacement: Option[String]) {
    def process(agent: String): Option[OS] = {
      val matcher = pattern.matcher(agent)
      if (!matcher.find()) return None
      osReplacement.map { replacement =>
        if (matcher.groupCount() >= 1)
          Pattern.compile(s"(${Pattern.quote("$1")})").matcher(replacement).replaceAll(matcher.group(1))
        else replacement
      }.orElse(matcher.groupAt(1)).map { family =>
        val major = v1Replacement.orElse(matcher.groupAt(2))
        val minor = v2Replacement.orElse(matcher.groupAt(3))
        val patch = v3Replacement.orElse(matcher.groupAt(4))
        val patchMinor = v4Replacement.orElse(matcher.groupAt(5))
        OS(family, major, minor, patch, patchMinor)
      }
    }
  }

  private object OSPattern {
    def fromMap(m: Map[String, String]) = m.get("regex").map { r =>
      OSPattern(Pattern.compile(r), m.get("os_replacement"), m.get("os_v1_replacement"), m.get("os_v2_replacement"),
        m.get("os_v3_replacement"), m.get("os_v4_replacement"))
    }
  }

  case class OSParser(patterns: List[OSPattern]) {
    def parse(agent: String) = patterns.foldLeft[Option[OS]](None) {
      case (None, pattern) => pattern.process(agent)
      case (result, _) => result
    }.getOrElse(OS("Other"))
  }

  object OSParser {
    def fromList(config: List[Map[String, String]]) = OSParser(config.flatMap(OSPattern.fromMap))
  }
}
