package org.uaparser.scala

case class DeviceParser(patterns: List[DevicePattern]) {
  def parse(agent: String): Device = patterns
    .foldLeft[Option[Device]](None) {
      case (None, pattern) => pattern.process(agent)
      case (result, _)     => result
    }
    .getOrElse(Device("Other"))
}

object DeviceParser {
  def fromList(config: List[Map[String, String]]): DeviceParser =
    DeviceParser(config.flatMap(DevicePattern.fromMap))
}
