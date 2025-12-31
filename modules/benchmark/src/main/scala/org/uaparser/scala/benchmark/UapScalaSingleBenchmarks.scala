package org.uaparser.scala.benchmark

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole

import org.uaparser.scala.Parser

@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
class UapScalaSingleBenchmarks {

  @Param(Array("early", "mid", "late", "no-match"))
  private var scenario: String = _

  private var parser: Parser = _

  private var deviceTestStr: String = _
  private var osTestStr: String = _
  private var userAgentTestStr: String = _
  private var allTestStr: String = _

  // The following are user-agent strings selected to match early, mid, and late regexes inside the uap-core regexes.yaml definitions
  private val earlyDevice =
    """AdsBot-Google-Mobile (+http://www.google.com/mobile/adsbot.html) Mozilla (iPhone; U; CPU iPhone OS 3 0 like Mac OS X) AppleWebKit (KHTML, like Gecko) Mobile Safari"""
  private val midDevice =
    """Mozilla/5.0 (Linux; Android 4.1.1; AEON Build/JRO03H) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.57 Safari/537.36 OPR/18.0.1290.66961"""
  private val lateDevice =
    """Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Safari/605.1.15"""

  private val earlyOs =
    """Mozilla/5.0 (Unknown; Linux armv7l) AppleWebKit/537.1+ (KHTML, like Gecko) Safari/537.1+ HbbTV/1.1.1 ( ;LGE ;NetCast 4.0 ;03.20.30 ;1.0M ;)"""
  private val midOs = """Yelp/8.2.1 CFNetwork/705.1 Darwin/14.0.0"""
  private val lateOs =
    """Mozilla/5.0 (TAS-AL00 Build/HUAWEITAS-AL00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/97.0.4692.98 Mobile Safari/537.36 T7/13.76 BDOS/1.0 (HarmonyOS 3.0.0) SP-engine/3.17.0 baiduboxapp/13.76.0.10 (Baidu; P1 12) NABar/1.0"""

  private val earlyUserAgent = """GeoEvent Server 10.5.1"""
  private val midUserAgent =
    """Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36 CCleaner/131.0.0.0"""
  private val lateUserAgent =
    """Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:102.0) Gecko/20100101 MullvadBrowser/102.13.0"""

  private val allEarly =
    """Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5X Build/MMB29P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.6367.201 Mobile Safari/537.36 (compatible; Google-InspectionTool/1.0;)"""
  private val allMid = """Mozilla/4.0 (compatible; MSIE 6.0; Windows CE; IEMobile 7.11) Sprint:MotoQ9c"""
  private val allLate =
    """Mozilla/5.0 (BB10; Touch) AppleWebKit/537.3+ (KHTML, like Gecko) Version/10.0.9.388 Mobile Safari/537.3+"""

  // we should have no match here
  private val noMatch = """DefinitelyNotAUserAgent/0.0 (totally; invalid) __benchmark_no_match__"""

  private def pick(early: String, mid: String, late: String): String = {
    scenario match {
      case "early"    => early
      case "mid"      => mid
      case "late"     => late
      case "no-match" => noMatch
    }
  }

  @Setup(Level.Trial)
  def setup(): Unit = {
    parser = BenchmarkSupport.loadParserForPinnedRegexes()
    deviceTestStr = pick(earlyDevice, midDevice, lateDevice)
    osTestStr = pick(earlyOs, midOs, lateOs)
    userAgentTestStr = pick(earlyUserAgent, midUserAgent, lateUserAgent)
    allTestStr = pick(allEarly, allMid, allLate)
  }

  @Benchmark def singleDevice(bh: Blackhole): Unit =
    bh.consume(parser.deviceParser.parse(deviceTestStr))

  @Benchmark def singleOs(bh: Blackhole): Unit =
    bh.consume(parser.osParser.parse(osTestStr))

  @Benchmark def singleUserAgent(bh: Blackhole): Unit =
    bh.consume(parser.userAgentParser.parse(userAgentTestStr))

  @Benchmark def singleAll(bh: Blackhole): Unit =
    bh.consume(parser.parse(allTestStr))
}
