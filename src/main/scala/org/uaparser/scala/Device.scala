package org.uaparser.scala

import java.util.regex.{Matcher, Pattern}

import org.uaparser.scala.MatcherOps._

case class Device(family: String, brand: Option[String] = None, model: Option[String] = None)

object Device {
  private[scala] def fromMap(m: Map[String, String]) = m.get("family").map(Device(_, m.get("brand"), m.get("model")))

  private[scala] case class DevicePattern(pattern: Pattern, familyReplacement: Option[String],
                                           brandReplacement: Option[String], modelReplacement: Option[String]) {
    def process(agent: String): Option[Device] = {
      val matcher = pattern.matcher(agent)
      if (!matcher.find()) return None
      val family = familyReplacement.map(r => replace(r, matcher)).orElse(matcher.groupAt(1)).getOrElse("Other")
      val brand = brandReplacement.map(r => replace(r, matcher)).filterNot(s => s.isEmpty)
      val model = modelReplacement.map(r => replace(r, matcher)).orElse(matcher.groupAt(1)).filterNot(s => s.isEmpty)
      Some(Device(family, brand, model))
    }

    def replace(replacement: String, matcher: Matcher) = {
      (if (replacement.contains("$") && matcher.groupCount() >= 1)  {
        (1 to matcher.groupCount()).foldLeft(replacement)((rep, i) => {
          val toInsert = if(matcher.group(i) != null) matcher.group(i) else ""
          rep.replaceFirst("\\$" + i, Matcher.quoteReplacement(toInsert))
        })
      } else replacement).trim
    }
  }

  private object DevicePattern {
    def fromMap(m: Map[String, String]) = m.get("regex").map { r =>
      val pattern = m.get("regex_flag").map(flag => Pattern.compile(r, Pattern.CASE_INSENSITIVE)).getOrElse(Pattern.compile(r))
      DevicePattern(pattern, m.get("device_replacement"), m.get("brand_replacement"), m.get("model_replacement"))
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
