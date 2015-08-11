package ua.parser

import java.io.InputStream

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.SafeConstructor
import ua.parser.Device.DeviceParser
import ua.parser.OS.OSParser
import ua.parser.UserAgent.UserAgentParser
import scala.collection.JavaConverters._
import java.util.{Map => JMap, List => JList}

case class Parser(userAgentParser: UserAgentParser, osParser: OSParser, deviceParser: DeviceParser)
    extends UserAgentStringParser {
  def parse(agent: String) = Client(userAgentParser.parse(agent), osParser.parse(agent), deviceParser.parse(agent))
}

object Parser {

  def create(source: InputStream) = {
    val yaml = new Yaml(new SafeConstructor)
    val javaConfig = yaml.load(source).asInstanceOf[JMap[String, JList[JMap[String, String]]]]
    val config = javaConfig.asScala.toMap.mapValues(_.asScala.toList.map(_.asScala.toMap.filterNot {
      case (_ , value) => value == null
    }))
    val userAgentParser = UserAgentParser.fromList(config.getOrElse("user_agent_parsers", List()))
    val osParser = OSParser.fromList(config.getOrElse("os_parsers", List()))
    val deviceParser = DeviceParser.fromList(config.getOrElse("device_parsers", List()))
    Parser(userAgentParser, osParser, deviceParser)
  }
  def get = create(this.getClass.getResourceAsStream("/regexes.yaml"))
}
