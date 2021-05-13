package org.uaparser.scala

import java.io.InputStream
import java.util.{ List => JList, Map => JMap }
import org.uaparser.scala.Device.DeviceParser
import org.uaparser.scala.OS.OSParser
import org.uaparser.scala.UserAgent.UserAgentParser
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.SafeConstructor
import scala.collection.JavaConverters._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util.Try

case class Parser(userAgentParser: UserAgentParser, osParser: OSParser, deviceParser: DeviceParser)
    extends UserAgentStringParser {
  def parse(agent: String): Client =
    Client(userAgentParser.parse(agent), osParser.parse(agent), deviceParser.parse(agent))

  def parParse(agent: String): Client = {
    val userAgentFuture = Future {userAgentParser.parse(agent)}
    val osFuture = Future {osParser.parse(agent)}
    val deviceFuture = Future {deviceParser.parse(agent)}
    val clientFuture = for {
      ua <- userAgentFuture
      os <- osFuture
      dv <- deviceFuture
    } yield Client(ua, os, dv)
    Await.result(clientFuture, Duration.Inf)
  }
}

object Parser {
  def fromInputStream(source: InputStream): Try[Parser] = Try {
    val yaml = new Yaml(new SafeConstructor)
    val javaConfig = yaml.load(source).asInstanceOf[JMap[String, JList[JMap[String, String]]]]
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
