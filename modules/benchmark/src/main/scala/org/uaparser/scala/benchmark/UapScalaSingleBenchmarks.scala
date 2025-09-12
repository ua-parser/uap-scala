package org.uaparser.scala.benchmark

import org.openjdk.jmh.annotations.{Benchmark, Scope, State}
import org.openjdk.jmh.infra.Blackhole

import org.uaparser.scala.Parser

@State(Scope.Benchmark)
class UapScalaSingleBenchmarks {

  // These agents were chosen because they correspond to valid agents that get evaluated by the last regex defined
  // in the current yaml file in the resources folder. So, they should take the most time to evaluate.
  val userAgentSingleTest =
    """Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:102.0) Gecko/20100101 MullvadBrowser/102.13.0"""
  val osSingleTest =
    """Mozilla/5.0 (TAS-AL00 Build/HUAWEITAS-AL00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/97.0.4692.98 Mobile Safari/537.36 T7/13.76 BDOS/1.0 (HarmonyOS 3.0.0) SP-engine/3.17.0 baiduboxapp/13.76.0.10 (Baidu; P1 12) NABar/1.0"""
  val deviceSingleTest =
    """Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.5 Safari/605.1.15"""

  // This is somewhere in the middle for all regexes.
  val allSingleTest = "(C)NokiaNXX/SymbianOS/9.1 Series60/3.0"

  var parser: Parser =
    Parser.fromInputStream(Thread.currentThread.getContextClassLoader.getResourceAsStream("regexes_@7388149c.yaml")).get

  @Benchmark
  def measureSingleStrDeviceParser(bh: Blackhole): Unit =
    bh.consume(parser.deviceParser.parse(deviceSingleTest))

  @Benchmark
  def measureSingleStrOsParser(bh: Blackhole): Unit =
    bh.consume(parser.osParser.parse(osSingleTest))

  @Benchmark
  def measureSingleStrUserAgentParser(bh: Blackhole): Unit =
    bh.consume(parser.userAgentParser.parse(userAgentSingleTest))

  @Benchmark
  def measureSingleStrAllParser(bh: Blackhole): Unit =
    bh.consume(parser.parse(allSingleTest))
}
