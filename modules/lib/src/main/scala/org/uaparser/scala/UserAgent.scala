package org.uaparser.scala

case class UserAgent(
    family: String,
    major: Option[String] = None,
    minor: Option[String] = None,
    patch: Option[String] = None
)

object UserAgent {
  private[scala] def fromMap(m: Map[String, String]) = m.get("family").map { family =>
    UserAgent(family, m.get("major"), m.get("minor"), m.get("patch"))
  }

  case class UserAgentParser(patterns: List[UserAgentPattern]) {
    def parse(agent: String): UserAgent = patterns
      .foldLeft[Option[UserAgent]](None) {
        case (None, pattern) => pattern.process(agent)
        case (result, _)     => result
      }
      .getOrElse(UserAgent("Other"))
  }

  object UserAgentParser {
    def fromList(config: List[Map[String, String]]): UserAgentParser =
      UserAgentParser(config.flatMap(UserAgentPattern.fromMap))
  }
}
