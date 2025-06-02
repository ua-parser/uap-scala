package org.uaparser.scala.benchmark

import scala.io.Source

import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

import org.uaparser.scala.Parser

@State(Scope.Benchmark)
class UapScalaBenchmarks {

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

  // an entire bundle of strings taken from the current suite of tests
  val allUserAgentStrings: List[String] = Source.fromResource("all-user-agents.txt").getLines().toList

  var parser: Parser =
    Parser.fromInputStream(Thread.currentThread.getContextClassLoader.getResourceAsStream("regexes_@7388149c.yaml")).get

  @Benchmark
  def measureSingleStrDeviceParser(): Unit = {
    parser.deviceParser.parse(deviceSingleTest)
  }

  @Benchmark
  def measureSingleStrOsParser(): Unit = {
    parser.osParser.parse(osSingleTest)
  }

  @Benchmark
  def measureSingleStrUserAgentParser(): Unit = {
    parser.userAgentParser.parse(userAgentSingleTest)
  }

  @Benchmark
  def measureSingleStrAllParser(): Unit = {
    parser.parse(allSingleTest)
  }

  @Benchmark
  def measureAllStrDeviceParser(): Unit = {
    allUserAgentStrings.foreach(parser.deviceParser.parse)
  }

  @Benchmark
  def measureAllStrOsParser(): Unit = {
    allUserAgentStrings.foreach(parser.osParser.parse)
  }

  @Benchmark
  def measureAllStrUserAgentParser(): Unit = {
    allUserAgentStrings.foreach(parser.userAgentParser.parse)
  }

  @Benchmark
  def measureAllStrAllParser(): Unit = {
    allUserAgentStrings.foreach(parser.parse)
  }
}
