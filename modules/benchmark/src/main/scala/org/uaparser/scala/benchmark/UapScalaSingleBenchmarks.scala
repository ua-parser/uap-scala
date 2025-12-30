package org.uaparser.scala.benchmark

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole

import org.uaparser.scala.Parser

@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
class UapScalaSingleBenchmarks {

  @Param(Array("best", "late", "noMatch"))
  private var scenario: String = _

  private var parser: Parser = _

  private var deviceTestStr: String = _
  private var osTestStr: String = _
  private var userAgentTestStr: String = _
  private var allTestStr: String = _

  // things that match late in the regex list
  private val lateUserAgent =
    """Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:102.0) Gecko/20100101 MullvadBrowser/102.13.0"""
  private val lateOs =
    """Mozilla/5.0 (TAS-AL00 Build/HUAWEITAS-AL00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/97.0.4692.98 Mobile Safari/537.36 T7/13.76 BDOS/1.0 (HarmonyOS 3.0.0) SP-engine/3.17.0 baiduboxapp/13.76.0.10 (Baidu; P1 12) NABar/1.0"""
  private val lateDevice =
    """Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.5 Safari/605.1.15"""
  private val midAll =
    "(C)NokiaNXX/SymbianOS/9.1 Series60/3.0"

  // things that match early in the regex list
  private val bestUserAgent =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
  private val bestOs =
    "Mozilla/5.0 (Linux; Android 13; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
  private val bestDevice =
    "Mozilla/5.0 (iPhone; CPU iPhone OS 16_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.5 Mobile/15E148 Safari/604.1"
  private val bestAll = bestUserAgent

  // we should have no match here
  private val noMatch =
    "DefinitelyNotAUserAgent/0.0 (totally; invalid) __benchmark_no_match__"

  private def pick(best: String, late: String, mid: String): String = {
    scenario match {
      case "best"    => best
      case "late"    => late
      case "noMatch" => noMatch
      case _         => mid
    }
  }

  @Setup(Level.Trial)
  def setup(): Unit = {
    parser = BenchmarkSupport.loadParserForPinnedRegexes()
    deviceTestStr = pick(bestDevice, lateDevice, lateDevice)
    osTestStr = pick(bestOs, lateOs, lateOs)
    userAgentTestStr = pick(bestUserAgent, lateUserAgent, lateUserAgent)
    allTestStr = pick(bestAll, midAll, midAll)
  }

  @Benchmark def single_device(bh: Blackhole): Unit =
    bh.consume(parser.deviceParser.parse(deviceTestStr))

  @Benchmark def single_os(bh: Blackhole): Unit =
    bh.consume(parser.osParser.parse(osTestStr))

  @Benchmark def single_userAgent(bh: Blackhole): Unit =
    bh.consume(parser.userAgentParser.parse(userAgentTestStr))

  @Benchmark def single_all(bh: Blackhole): Unit =
    bh.consume(parser.parse(allTestStr))
}
