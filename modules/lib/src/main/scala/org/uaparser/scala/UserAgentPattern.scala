package org.uaparser.scala

import java.util.regex.{Matcher, Pattern}

import org.uaparser.scala.MatcherOps.MatcherImprovements

private[scala] case class UserAgentPattern(
    pattern: Pattern,
    familyReplacement: Option[String],
    v1Replacement: Option[String],
    v2Replacement: Option[String],
    v3Replacement: Option[String]
) {
  def process(agent: String): Option[UserAgent] = {
    val matcher = pattern.matcher(agent)
    if (!matcher.find()) return None
    familyReplacement
      .map { replacement =>
        if (replacement.contains("$1") && matcher.groupCount() >= 1) {
          replacement.replaceFirst("\\$1", Matcher.quoteReplacement(matcher.group(1)))
        } else replacement
      }
      .orElse(matcher.groupAt(1))
      .map { family =>
        val major = v1Replacement.orElse(matcher.groupAt(2)).filter(_.nonEmpty)
        val minor = v2Replacement.orElse(matcher.groupAt(3)).filter(_.nonEmpty)
        val patch = v3Replacement.orElse(matcher.groupAt(4)).filter(_.nonEmpty)
        UserAgent(family, major, minor, patch)
      }
  }
}

private object UserAgentPattern {
  def fromMap(config: Map[String, String]): Option[UserAgentPattern] = config.get("regex").map { r =>
    UserAgentPattern(
      Pattern.compile(r),
      config.get("family_replacement"),
      config.get("v1_replacement"),
      config.get("v2_replacement"),
      config.get("v3_replacement")
    )
  }
}
