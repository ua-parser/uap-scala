package org.uaparser.scala

case class Device(family: String, brand: Option[String] = None, model: Option[String] = None)

object Device {
  private[scala] def fromMap(m: Map[String, String]) = m.get("family").map(Device(_, m.get("brand"), m.get("model")))

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
}
