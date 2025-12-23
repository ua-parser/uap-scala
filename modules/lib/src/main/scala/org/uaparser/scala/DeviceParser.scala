package org.uaparser.scala

case class DeviceParser(patterns: List[DevicePattern]) {
  private val patternsArray = patterns.toArray
  private val length = patternsArray.length
  def parse(agent: String): Device = {
    var i = 0
    while (i < length) {
      patternsArray(i).process(agent) match {
        case Some(d) => return d
        case None    => ()
      }
      i += 1
    }
    DeviceParser.Other
  }
}

object DeviceParser {
  private val Other = Device("Other")

  def fromList(config: List[Map[String, String]]): DeviceParser =
    DeviceParser(config.flatMap(DevicePattern.fromMap))
}
