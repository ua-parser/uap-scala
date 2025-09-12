package org.uaparser.scala

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
