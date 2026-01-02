package org.uaparser.scala

import java.util.regex.Pattern

import org.uaparser.scala.MatcherOps.MatcherImprovements

private[scala] case class DevicePattern(
    pattern: Pattern,
    familyReplacement: Option[String],
    brandReplacement: Option[String],
    modelReplacement: Option[String]
) {

  def process(agent: String): Option[Device] = {
    val matcher = pattern.matcher(agent)
    if (!matcher.find()) None
    else {
      val family = familyReplacement
        .map(r => Util.patternReplacementWithMatcherGroups(r, matcher, agent))
        .orElse(matcher.groupAt(1))
      val brand =
        brandReplacement.map(r => Util.patternReplacementWithMatcherGroups(r, matcher, agent)).filterNot(s => s.isEmpty)
      val model = modelReplacement
        .map(r => Util.patternReplacementWithMatcherGroups(r, matcher, agent))
        .orElse(matcher.groupAt(1))
        .filterNot(s => s.isEmpty)
      family.map(Device(_, brand, model))
    }
  }
}

private object DevicePattern {
  def fromMap(m: Map[String, String]): Option[DevicePattern] = m.get("regex").map { r =>
    val pattern =
      m.get("regex_flag").map(_ => Pattern.compile(r, Pattern.CASE_INSENSITIVE)).getOrElse(Pattern.compile(r))
    DevicePattern(pattern, m.get("device_replacement"), m.get("brand_replacement"), m.get("model_replacement"))
  }
}
