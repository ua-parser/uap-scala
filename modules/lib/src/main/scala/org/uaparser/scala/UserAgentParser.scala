package org.uaparser.scala

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
