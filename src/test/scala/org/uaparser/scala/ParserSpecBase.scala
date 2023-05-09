package org.uaparser.scala

import org.specs2.mutable.Specification
import org.yaml.snakeyaml.{LoaderOptions, Yaml}
import java.io.{ByteArrayInputStream, InputStream}
import java.nio.charset.StandardCharsets
import java.util.{List => JList, Map => JMap}

import scala.collection.JavaConverters._

trait ParserSpecBase extends Specification {
  sequential

  val parser: UserAgentStringParser
  def createFromStream(stream: InputStream): UserAgentStringParser

  "Parser should" >> {
    val yaml = {
      val maxFileSizeBytes = 5 * 1024 * 1024 // 5 MB
      val loaderOptions = new LoaderOptions()
      loaderOptions.setCodePointLimit(maxFileSizeBytes)
      new Yaml(loaderOptions)
    }

    def readCasesConfig(resource: String): List[Map[String, String]] = {
      val stream = this.getClass.getResourceAsStream(resource)
      val cases = yaml.load[JMap[String, JList[JMap[String, String]]]](stream)
        .asScala.toMap.mapValues(_.asScala.toList.map(_.asScala.toMap))
      cases.getOrElse("test_cases", List()).filterNot(_.contains("js_ua")).map { config =>
        config.filterNot { case (_, value) => value eq null }
      }
    }

    "parse basic ua" >> {
      val cases = List(
        "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; fr; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 ,gzip(gfe),gzip(gfe)" ->
          Client(UserAgent("Firefox", Some("3"), Some("5"), Some("5")), OS("Mac OS X", Some("10"), Some("4")), Device("Mac", Some("Apple"), Some("Mac"))),
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

    "properly quote all os replacements" >> {
      val testConfig =
        """
          |os_parsers:
          |  - regex: '(\w+\s+Mac OS X\s+\w+\s+(\d+).(\d+).(\d+).*)'
          |    os_replacement: 'Mac OS X'
          |    os_v1_replacement: '$2'
          |    os_v2_replacement: '$3'
          |    os_v3_replacement: '$4'
        """.stripMargin

      val stream = new ByteArrayInputStream(testConfig.getBytes(StandardCharsets.UTF_8))
      val parser = createFromStream(stream)
      val client = parser.parse("""ABC12\34 (Intelx64 Mac OS X Version 10.12.6 OH-HAI=/^.^\=)""")
      client.os.family must beEqualTo("Mac OS X")
      client.os.major must beSome("10")
      client.os.minor must beSome("12")
      client.os.patch must beSome("6")
    }

    "properly quote all user agent replacements" >> {
      val testConfig =
         """
           |user_agent_parsers:
           |  - regex: 'ABC([\\0-9]+)'
           |    family_replacement: 'ABC ($1)'
           |    v1_replacement: '1'
           |    v2_replacement: '0'
           |    v3_replacement: '2'
         """.stripMargin

      val stream = new ByteArrayInputStream(testConfig.getBytes(StandardCharsets.UTF_8))
      val parser = createFromStream(stream)
      val client = parser.parse("""ABC12\34; OH-HAI=/^.^\=""")
      client.userAgent.family must beEqualTo("""ABC (12\34)""")
      client.userAgent.major must beSome("1")
      client.userAgent.minor must beSome("0")
      client.userAgent.patch must beSome("2")
    }

    "properly handle empty config" >> {
      val stream = new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8))
      val parser = createFromStream(stream)
      val client = parser.parse("""ABC12\34 (CashPhone-$9.0.1 CatOS OH-HAI=/^.^\=)""")
      client.userAgent.family must beEqualTo("""Other""")
      client.os.family must beEqualTo("Other")
      client.device.family must beEqualTo("Other")
    }

    "properly parse an user agent with None for missing information" >> {
      val testConfig =
        """
          |user_agent_parsers:
          |  - regex: '(ABC) (\d+?\.|)(\d+|)(\d+|);'
        """.stripMargin
      val stream = new ByteArrayInputStream(testConfig.getBytes(StandardCharsets.UTF_8))
      val parser = createFromStream(stream)
      val client = parser.parse("""(compatible; ABC ; OH-HAI=/^.^\=""")
      client.userAgent.family must beEqualTo("ABC")
      client.userAgent.major must beNone
      client.userAgent.minor must beNone
      client.userAgent.patch must beNone
    }

    "properly parse user agents" >> {
      List("/tests/test_ua.yaml", "/test_resources/firefox_user_agent_strings.yaml",
        "/test_resources/pgts_browser_list.yaml").flatMap { file =>
        readCasesConfig(file).map { c =>
          parser.parse(c("user_agent_string")).userAgent must beEqualTo(UserAgent.fromMap(c).get)
        }
      }
    }

    "properly parse os" >> {
      List("/tests/test_os.yaml", "/test_resources/additional_os_tests.yaml").flatMap { file =>
        readCasesConfig(file).map { c =>
          parser.parse(c("user_agent_string")).os must beEqualTo(OS.fromMap(c).get)
        }
      }
    }
    "properly parse device" >> {
      readCasesConfig("/tests/test_device.yaml").map { c =>
        parser.parse(c("user_agent_string")).device must beEqualTo(Device.fromMap(c).get)
      }
    }
  }
}
