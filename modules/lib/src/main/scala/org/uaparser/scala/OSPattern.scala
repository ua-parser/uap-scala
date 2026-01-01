package org.uaparser.scala

import java.util.regex.{Matcher, Pattern}

import scala.collection.mutable.ListBuffer

import org.uaparser.scala.MatcherOps.MatcherImprovements

private[scala] final case class OSPattern(
    pattern: Pattern,
    osReplacement: Option[OSPattern.FamilyReplacement],
    v1Replacement: Option[OSPattern.VersionReplacement],
    v2Replacement: Option[OSPattern.VersionReplacement],
    v3Replacement: Option[OSPattern.VersionReplacement],
    v4Replacement: Option[OSPattern.VersionReplacement]
) {

  def process(agent: String): Option[OS] = {
    val m = pattern.matcher(agent)
    if (!m.find()) None
    else {
      val familyOpt: Option[String] =
        osReplacement match {
          case Some(rep) => Some(rep.render(agent, m))
          case None      => m.groupAt(1)
        }

      familyOpt.map { family =>
        val major = OSPattern.resolveVersion(v1Replacement, m, fallbackGroup = 2)
        val minor = OSPattern.resolveVersion(v2Replacement, m, fallbackGroup = 3)
        val patch = OSPattern.resolveVersion(v3Replacement, m, fallbackGroup = 4)
        val patchMinor = OSPattern.resolveVersion(v4Replacement, m, fallbackGroup = 5)
        OS(family, major, minor, patch, patchMinor)
      }
    }
  }
}

private object OSPattern {

  private val Dollar1 = "$1"

  sealed trait FamilyReplacement {
    def render(agent: String, m: Matcher): String
  }

  private final case class FamilyLiteral(value: String) extends FamilyReplacement {
    override def render(agent: String, m: Matcher): String = value
  }
  private final case class FamilyWithGroup1(parts: Array[String]) extends FamilyReplacement {

    // We insert group 1 between each part.
    override def render(agent: String, m: Matcher): String = {
      val stringBuilder = new java.lang.StringBuilder()
      val hasGroup1 = m.groupCount() >= 1 && m.start(1) >= 0
      val partsLength = parts.length

      var i = 0
      parts.foreach { part =>
        stringBuilder.append(part)
        if (i < partsLength - 1) {
          if (hasGroup1) stringBuilder.append(agent, m.start(1), m.end(1))
        }
        i += 1
      }
      stringBuilder.toString
    }
  }

  private def compileFamilyReplacement(replacementDef: String): FamilyReplacement = {
    val first = replacementDef.indexOf(Dollar1)
    if (first < 0) FamilyLiteral(replacementDef)
    else {
      // Split by "$1". Keep empty segments.
      val buf = ListBuffer.empty[String]
      var from = 0
      var idx = first
      while (idx >= 0) {
        buf += replacementDef.substring(from, idx)
        from = idx + Dollar1.length
        idx = replacementDef.indexOf(Dollar1, from)
      }
      buf += replacementDef.substring(from)
      FamilyWithGroup1(buf.toArray)
    }
  }

  sealed trait VersionReplacement
  private final case class VersionLiteral(value: String) extends VersionReplacement
  private final case class VersionGroupRef(group: Int) extends VersionReplacement

  private def compileVersionReplacement(replacementDef: String): VersionReplacement = {
    // Treat only the whole string "$<digits>" as a group reference, otherwise it will be treated as a literal.
    if (replacementDef != null && replacementDef.length >= 2 && replacementDef.charAt(0) == '$') {
      var i = 1
      var n = 0
      var hasDigits = false
      while (i < replacementDef.length) {
        val ch = replacementDef.charAt(i)
        if (ch >= '0' && ch <= '9') {
          hasDigits = true
          n = n * 10 + (ch - '0')
          i += 1
        } else {
          return VersionLiteral(replacementDef)
        }
      }
      if (hasDigits && n > 0) VersionGroupRef(n) else VersionLiteral(replacementDef)
    } else VersionLiteral(replacementDef)
  }

  private def evalVersion(rep: VersionReplacement, m: Matcher): Option[String] =
    rep match {
      case VersionLiteral(v)   => if (v == null || v.isEmpty) None else Some(v)
      case VersionGroupRef(gr) => m.groupAt(gr)
    }

  // - replacement is a backref and missing, fall back to the default captured group
  // - replacement is a literal, use it (unless empty)
  // - no replacement provided, use fallback captured group
  private def resolveVersion(repOpt: Option[VersionReplacement], matcher: Matcher, fallbackGroup: Int): Option[String] =
    repOpt match {
      case Some(VersionGroupRef(gr)) => matcher.groupAt(gr).orElse(matcher.groupAt(fallbackGroup))
      case Some(litOrOther)          => evalVersion(litOrOther, matcher).orElse(matcher.groupAt(fallbackGroup))
      case None                      => matcher.groupAt(fallbackGroup)
    }

  def fromMap(m: Map[String, String]): Option[OSPattern] =
    m.get("regex").map { r =>
      val osRep = m.get("os_replacement").map(compileFamilyReplacement)
      val v1 = m.get("os_v1_replacement").map(compileVersionReplacement)
      val v2 = m.get("os_v2_replacement").map(compileVersionReplacement)
      val v3 = m.get("os_v3_replacement").map(compileVersionReplacement)
      val v4 = m.get("os_v4_replacement").map(compileVersionReplacement)

      OSPattern(
        pattern = Pattern.compile(r),
        osReplacement = osRep,
        v1Replacement = v1,
        v2Replacement = v2,
        v3Replacement = v3,
        v4Replacement = v4
      )
    }
}
