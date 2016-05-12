package ua.parser

import java.util.regex.{ Matcher, Pattern }

import MatcherOps._

import scala.annotation.tailrec

case class Device(family: String)

object Device {
  private[parser] def fromMap(m: Map[String, String]) = m.get("family").map(Device(_))

  private[parser] case class DevicePattern(pattern: Pattern, familyReplacement: Option[String]) {
    def process(agent: String): Option[Device] = {
      val matcher = pattern.matcher(agent)
      if (!matcher.find()) return None
      familyReplacement.map { replacement =>
        if (replacement.contains("$") && matcher.groupCount() >= 1) {
          @tailrec
          def replace(i: Int, replaced: String): String = {
            if (i < 1) replaced
            else {
              val matched = if (matcher.group(i) == null) "" else matcher.group(i)
              replace(i - 1, replaced.replaceAll(s"\\$$$i", Matcher.quoteReplacement(matched)))
            }
          }
          replace(matcher.groupCount, replacement).trim
        } else replacement
      }.orElse(matcher.groupAt(1)).map(Device(_))
    }
  }

  private object DevicePattern {
    def fromMap(m: Map[String, String]) = m.get("regex").map { r =>
      val flag = if (m.get("regex_flag").getOrElse("") == "i") Pattern.CASE_INSENSITIVE else 0
      DevicePattern(Pattern.compile(r, flag), m.get("device_replacement"))
    }
  }

  case class DeviceParser(patterns: List[DevicePattern]) {
    def parse(agent: String) = patterns.foldLeft[Option[Device]](None) {
      case (None, pattern) => pattern.process(agent)
      case (result, _) => result
    }.getOrElse(Device("Other"))
  }

  object DeviceParser {
    def fromList(config: List[Map[String, String]]) = DeviceParser(config.flatMap(DevicePattern.fromMap))
  }
}
