package org.uaparser.scala

import MatcherOps._
import java.util.regex.{Matcher, Pattern}

case class UserAgent(family: String, major: Option[String] = None, minor: Option[String] = None,
                     patch: Option[String] = None)

object UserAgent {
  private[scala] def fromMap(m: Map[String, String]) = m.get("family").map { family =>
    UserAgent(family, m.get("major"), m.get("minor"), m.get("patch"))
  }

  private[scala] case class UserAgentPattern(pattern: Pattern, familyReplacement: Option[String],
                                      v1Replacement: Option[String], v2Replacement: Option[String],
                                      v3Replacement: Option[String]) {
    def process(agent: String): Option[UserAgent] = {
      val matcher = pattern.matcher(agent)
      if (!matcher.find()) return None
      familyReplacement.map { replacement =>
        if (replacement.contains("$1") && matcher.groupCount() >= 1) {
          replacement.replaceFirst("\\$1", Matcher.quoteReplacement(matcher.group(1)))
        } else replacement
      }.orElse(matcher.groupAt(1)).map { family =>
        val major = v1Replacement.orElse(matcher.groupAt(2))
        val minor = v2Replacement.orElse(matcher.groupAt(3))
        val patch = v3Replacement.orElse(matcher.groupAt(4))
        UserAgent(family, major, minor, patch)
      }
    }
  }

  private object UserAgentPattern {
    def fromMap(config: Map[String, String]): Option[UserAgentPattern] = config.get("regex").map { r =>
      UserAgentPattern(Pattern.compile(r), config.get("family_replacement"), config.get("v1_replacement"),
        config.get("v2_replacement"), config.get("v3_replacement"))
    }
  }

  case class UserAgentParser(patterns: List[UserAgentPattern]) {
    def parse(agent: String): UserAgent = patterns.foldLeft[Option[UserAgent]](None) {
      case (None, pattern) => pattern.process(agent)
      case (result, _) => result
    }.getOrElse(UserAgent("Other"))
  }

  object UserAgentParser {
    def fromList(config: List[Map[String, String]]): UserAgentParser =
      UserAgentParser(config.map(UserAgentPattern.fromMap).flatten)
  }
}
