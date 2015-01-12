package ua.parser

import java.util.regex.{Matcher, Pattern}
import MatcherOps._

case class Device(family: String)

object Device {
  private[parser] def fromMap(m: Map[String, String]) = m.get("family").map(Device(_))

  private[parser] case class DevicePattern(pattern: Pattern, familyReplacement: Option[String]) {
    def process(agent: String): Option[Device] = {
      val matcher = pattern.matcher(agent)
      if (!matcher.find()) return None
      familyReplacement.map { replacement =>
        if (replacement.contains("$1") && matcher.groupCount() >= 1)  {
          replacement.replaceFirst("\\$1", Matcher.quoteReplacement(matcher.group(1)))
        } else replacement
      }.orElse(matcher.groupAt(1)).map(Device(_))
    }
  }

  private object DevicePattern {
    def fromMap(m: Map[String, String]) = m.get("regex").map { r =>
      DevicePattern(Pattern.compile(r), m.get("device_replacement"))
    }
  }

  case class DeviceParser(patterns: List[DevicePattern]) {
    def parse(agent: String) = patterns.foldLeft[Option[Device]](None) {
      case (None, pattern) => pattern.process(agent)
      case (result, _) => result
    }.getOrElse(Device("Other"))
  }

  object DeviceParser {
    def fromList(config: List[Map[String, String]]) = DeviceParser(config.map(DevicePattern.fromMap).flatten)
  }
}
