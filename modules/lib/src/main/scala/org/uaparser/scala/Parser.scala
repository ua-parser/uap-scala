package org.uaparser.scala

import java.io.InputStream

import scala.util.Try

import org.uaparser.scala.Device.DeviceParser
import org.uaparser.scala.OS.OSParser
import org.uaparser.scala.UserAgent.UserAgentParser

case class Parser(userAgentParser: UserAgentParser, osParser: OSParser, deviceParser: DeviceParser)
    extends UserAgentStringParser {
  def parse(agent: String): Client =
    Client(userAgentParser.parse(agent), osParser.parse(agent), deviceParser.parse(agent))
}

object Parser {
  def fromInputStream(source: InputStream): Try[Parser] = Try {
    val config = YamlUtil.loadYamlAsMap(source)
    val userAgentParser = UserAgentParser.fromList(config.getOrElse("user_agent_parsers", Nil))
    val osParser = OSParser.fromList(config.getOrElse("os_parsers", Nil))
    val deviceParser = DeviceParser.fromList(config.getOrElse("device_parsers", Nil))
    Parser(userAgentParser, osParser, deviceParser)
  }
  def default: Parser = fromInputStream(this.getClass.getResourceAsStream("/regexes.yaml")).get
}
