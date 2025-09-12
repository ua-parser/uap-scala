package org.uaparser.scala

case class OS(
    family: String,
    major: Option[String] = None,
    minor: Option[String] = None,
    patch: Option[String] = None,
    patchMinor: Option[String] = None
)

object OS {
  private[scala] def fromMap(m: Map[String, String]) = m.get("family").map { family =>
    OS(family, m.get("major"), m.get("minor"), m.get("patch"), m.get("patch_minor"))
  }

  case class OSParser(patterns: List[OSPattern]) {
    def parse(agent: String): OS = patterns
      .foldLeft[Option[OS]](None) {
        case (None, pattern) => pattern.process(agent)
        case (result, _)     => result
      }
      .getOrElse(OS("Other"))
  }

  object OSParser {
    def fromList(config: List[Map[String, String]]): OSParser = OSParser(config.flatMap(OSPattern.fromMap))
  }
}
