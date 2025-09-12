package org.uaparser.scala

import java.util.regex.{Matcher, Pattern}

import scala.util.control.Exception.allCatch

import org.uaparser.scala.MatcherOps.MatcherImprovements
import org.uaparser.scala.OSPattern.{replaceBackreference, replacementBack1}

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
    if (!matcher.find()) None
    else {
      osReplacement
        .map(replacementBack1(matcher))
        .orElse(matcher.groupAt(1))
        .map { family =>
          val major = v1Replacement.flatMap(replaceBackreference(matcher)).orElse(matcher.groupAt(2))
          val minor = v2Replacement.flatMap(replaceBackreference(matcher)).orElse(matcher.groupAt(3))
          val patch = v3Replacement.flatMap(replaceBackreference(matcher)).orElse(matcher.groupAt(4))
          val patchMinor = v4Replacement.flatMap(replaceBackreference(matcher)).orElse(matcher.groupAt(5))
          OS(family, major, minor, patch, patchMinor)
        }
    }
  }
}

private object OSPattern {
  private[this] val quotedBack1: Pattern = Pattern.compile(s"(${Pattern.quote("$1")})")

  private def getBackreferenceGroup(replacement: String): Option[Int] =
    for {
      ref <- Option(replacement).filter(_.contains("$"))
      groupOpt = allCatch.opt(ref.substring(1).toInt)
      group <- groupOpt
    } yield group

  private def replacementBack1(matcher: Matcher)(replacement: String): String =
    if (matcher.groupCount() >= 1) {
      quotedBack1.matcher(replacement).replaceAll(matcher.group(1))
    } else replacement

  private def replaceBackreference(matcher: Matcher)(replacement: String): Option[String] =
    getBackreferenceGroup(replacement) match {
      case Some(group) => matcher.groupAt(group)
      case None        => Some(replacement)
    }

  def fromMap(m: Map[String, String]): Option[OSPattern] = m.get("regex").map { r =>
    OSPattern(
      Pattern.compile(r),
      m.get("os_replacement"),
      m.get("os_v1_replacement"),
      m.get("os_v2_replacement"),
      m.get("os_v3_replacement"),
      m.get("os_v4_replacement")
    )
  }
}
