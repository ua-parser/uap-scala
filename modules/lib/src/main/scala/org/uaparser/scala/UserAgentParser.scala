package org.uaparser.scala

case class UserAgentParser(patterns: List[UserAgentPattern]) {
  private val patternsArray = patterns.toArray
  private val length = patternsArray.length
  def parse(agent: String): UserAgent = {
    var i = 0
    while (i < length) {
      patternsArray(i).process(agent) match {
        case Some(d) => return d
        case None    => ()
      }
      i += 1
    }
    UserAgentParser.Other
  }
}

object UserAgentParser {
  private val Other = UserAgent("Other")
  def fromList(config: List[Map[String, String]]): UserAgentParser =
    UserAgentParser(config.flatMap(UserAgentPattern.fromMap))
}
