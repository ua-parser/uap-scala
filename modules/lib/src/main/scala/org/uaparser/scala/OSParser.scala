package org.uaparser.scala

case class OSParser(patterns: List[OSPattern]) {
  private val patternsArray = patterns.toArray
  private val length = patternsArray.length
  def parse(agent: String): OS = {
    var i = 0
    while (i < length) {
      patternsArray(i).process(agent) match {
        case Some(d) => return d
        case None    => ()
      }
      i += 1
    }
    OSParser.Other
  }
}

object OSParser {
  private val Other = OS("Other")
  def fromList(config: List[Map[String, String]]): OSParser = OSParser(config.flatMap(OSPattern.fromMap))
}
