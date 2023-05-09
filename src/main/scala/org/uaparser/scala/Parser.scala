package org.uaparser.scala

import java.io.InputStream
import java.util.{List => JList, Map => JMap}

import org.uaparser.scala.Device.DeviceParser
import org.uaparser.scala.OS.OSParser
import org.uaparser.scala.UserAgent.UserAgentParser
import org.yaml.snakeyaml.{LoaderOptions, Yaml}
import org.yaml.snakeyaml.constructor.SafeConstructor
import scala.collection.JavaConverters._
import scala.util.Try

case class Parser(userAgentParser: UserAgentParser, osParser: OSParser, deviceParser: DeviceParser)
    extends UserAgentStringParser {
  def parse(agent: String): Client =
    Client(userAgentParser.parse(agent), osParser.parse(agent), deviceParser.parse(agent))
}

object Parser {
  def fromInputStream(source: InputStream): Try[Parser] = Try {
    val yaml = new Yaml(new SafeConstructor(new LoaderOptions))
    val javaConfig = yaml.load[JMap[String, JList[JMap[String, String]]]](source)
    val config = javaConfig.asScala.toMap.mapValues(_.asScala.toList.map(_.asScala.toMap.filterNot {
      case (_ , value) => value eq null
    }))
    val userAgentParser = UserAgentParser.fromList(config.getOrElse("user_agent_parsers", Nil))
    val osParser = OSParser.fromList(config.getOrElse("os_parsers", Nil))
    val deviceParser = DeviceParser.fromList(config.getOrElse("device_parsers", Nil))
    Parser(userAgentParser, osParser, deviceParser)
  }
  def default: Parser = fromInputStream(this.getClass.getResourceAsStream("/regexes.yaml")).get
}
