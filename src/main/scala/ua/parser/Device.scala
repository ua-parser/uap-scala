package ua.parser

import scala.util.matching.Regex

case class Device(family: String)

object Device {
  private[parser] final val SUBSTITUTIONS_REGEX = """\$\d""".r

  private[parser] def fromMap(m: Map[String, String]) = Option(Device(m("family")))

  private[parser] case class DevicePattern(pattern: Regex, familyReplacement: Option[String]) {

    def process(agent: String): Option[Device] = {

      val matches = pattern.findFirstMatchIn(agent)

      matches flatMap { matchedGroups =>
        familyReplacement.map { replacement =>
          if (replacement.contains("$")) {

            SUBSTITUTIONS_REGEX.replaceAllIn(replacement, m =>

              m.matched.drop(1).toInt match {
                case n if matchedGroups.groupCount >= n && matchedGroups.group(n) != null =>
                  Regex.quoteReplacement(matchedGroups.group(n))
                case _ => ""
              }

            ).trim
          } else replacement
        }.orElse(Option(matchedGroups.group(1))).map(Device(_))
      }
    }
  }

  private object DevicePattern {
    def fromMap(m: Map[String, String]) = m.get("regex").map { r =>
      m.get("regex_flag") match { // match insensitive or sensitive regex matching flag
        case Some("i") => DevicePattern(("(?i)" + r).r, m.get("device_replacement"))
        case _ => DevicePattern(r.r, m.get("device_replacement"))
      }
    }
  }

  case class DeviceParser(patterns: List[DevicePattern]) {
    def parse(agent: String) = {
      patterns.foldLeft[Option[Device]](None) {
        case (None, pattern) => pattern.process(agent)
        case (result, _) => result
      }.getOrElse(Device("Other"))
    }
  }

  object DeviceParser {
    def fromList(config: List[Map[String, String]]) = DeviceParser(config.flatMap(DevicePattern.fromMap))
  }

}
