package org.uaparser.scala

import org.specs2.mutable.Specification
import org.yaml.snakeyaml.Yaml
import java.io.{ ByteArrayInputStream, InputStream }
import java.nio.charset.StandardCharsets
import java.util.{ Map => JMap, List => JList}
import scala.collection.JavaConverters._

trait ParserSpecBase extends Specification {
  sequential

  val parser: UserAgentStringParser
  def createFromStream(stream: InputStream): UserAgentStringParser

  "Parser should" >> {
    val yaml = new Yaml()

    def readCasesConfig(resource: String): List[Map[String, String]] = {
      val stream = this.getClass.getResourceAsStream(resource)
      val cases = yaml.load(stream).asInstanceOf[JMap[String, JList[JMap[String, String]]]]
        .asScala.toMap.mapValues(_.asScala.toList.map(_.asScala.toMap))
      cases.getOrElse("test_cases", List()).filterNot(_.contains("js_ua")).map { config =>
        config.filterNot { case (_, value) => value eq null }
      }
    }

    "parse basic ua" >> {
      val cases = List(
        "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; fr; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 ,gzip(gfe),gzip(gfe)" ->
          Client(UserAgent("Firefox", Some("3"), Some("5"), Some("5")), OS("Mac OS X", Some("10"), Some("4")), Device("Other")),
        "Mozilla/5.0 (iPhone; CPU iPhone OS 5_1_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B206 Safari/7534.48.3" ->
          Client(UserAgent("Mobile Safari", Some("5"), Some("1")), OS("iOS", Some("5"), Some("1"), Some("1")), Device("iPhone", Some("Apple"), Some("iPhone")))
      )
      cases.map { case (agent, expected) =>
        parser.parse(agent) must beEqualTo(expected)
      }
    }

    "properly quote replacements" >> {
      val testConfig =
        """
          |user_agent_parsers:
          |  - regex: 'ABC([\\0-9]+)'
          |    family_replacement: 'ABC ($1)'
          |os_parsers:
          |  - regex: 'CatOS OH-HAI=/\^\.\^\\='
          |    os_replacement: 'CatOS 9000'
          |device_parsers:
          |  - regex: 'CashPhone-([\$0-9]+)\.(\d+)\.(\d+)'
          |    device_replacement: 'CashPhone $1'
        """.stripMargin
      val stream = new ByteArrayInputStream(testConfig.getBytes(StandardCharsets.UTF_8))
      val parser = createFromStream(stream)
      val client = parser.parse("""ABC12\34 (CashPhone-$9.0.1 CatOS OH-HAI=/^.^\=)""")
      client.userAgent.family must beEqualTo("""ABC (12\34)""")
      client.os.family must beEqualTo("CatOS 9000")
      client.device.family must beEqualTo("CashPhone $9")
    }

    "properly handle empty config" >> {
      val stream = new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8))
      val parser = createFromStream(stream)
      val client = parser.parse("""ABC12\34 (CashPhone-$9.0.1 CatOS OH-HAI=/^.^\=)""")
      client.userAgent.family must beEqualTo("""Other""")
      client.os.family must beEqualTo("Other")
      client.device.family must beEqualTo("Other")
    }

    "properly parse user agents" >> {
      List("/tests/test_ua.yaml", "/test_resources/firefox_user_agent_strings.yaml",
        "/test_resources/pgts_browser_list.yaml").map { file =>
        readCasesConfig(file).map { c =>
          parser.parse(c("user_agent_string")).userAgent must beEqualTo(UserAgent.fromMap(c).get)
        }
      }.flatten
    }

    "properly parse os" >> {
      List("/tests/test_os.yaml", "/test_resources/additional_os_tests.yaml").map { file =>
        readCasesConfig(file).map { c =>
          parser.parse(c("user_agent_string")).os must beEqualTo(OS.fromMap(c).get)
        }
      }.flatten
    }
    "properly parse device" >> {
      readCasesConfig("/tests/test_device.yaml").map { c =>
        parser.parse(c("user_agent_string")).device must beEqualTo(Device.fromMap(c).get)
      }
    }
  }
}
