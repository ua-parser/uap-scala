package org.uaparser.scala.benchmark

import org.openjdk.jmh.annotations.{Benchmark, Scope, Setup, State}
import org.uaparser.scala.Parser

@State(Scope.Benchmark)
class UapScalaBenchmarks {
  val smallUserAgentString = "Ice"
  val bigUserAgentString = """Mozilla/5.0 (Windows; Windows NT 6.1; WOW64; rv:2.0b8pre; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; MS-RTC LM 8; OfficeLiveConnector.1.4; OfficeLivePatch.1.3; SLCC1; SLCC2; Media Center PC 6.0; GTB6.4; InfoPath.2; en-US; FunWebProducts; Zango 10.1.181.0; SV1; PRTG Network Monitor (www.paessler.com)) Gecko/20101114 Firefox/4.0b8pre QuickTime/7.6.2 Songbird/1.1.2 Web-sniffer/1.0.36 lftp/3.7.4 libwww-perl/5.820 GSiteCrawler/v1.12 rev. 260 Snoopy v1.2"""

  var parser: Parser = _

  @Setup
  def prepare(): Unit = {
    parser = Parser.default
  }

  @Benchmark
  def measureBigDeviceParser(): Unit = {
    parser.deviceParser.parse(bigUserAgentString)
  }

  @Benchmark
  def measureBigOsParser(): Unit = {
    parser.osParser.parse(bigUserAgentString)
  }

  @Benchmark
  def measureBigUserAgentParser(): Unit = {
    parser.userAgentParser.parse(bigUserAgentString)
  }

  @Benchmark
  def measureBigAllParser(): Unit = {
    parser.parse(bigUserAgentString)
  }

  @Benchmark
  def measureSmallDeviceParser(): Unit = {
    parser.deviceParser.parse(smallUserAgentString)
  }

  @Benchmark
  def measureSmallOsParser(): Unit = {
    parser.osParser.parse(smallUserAgentString)
  }

  @Benchmark
  def measureSmallUserAgentParser(): Unit = {
    parser.userAgentParser.parse(smallUserAgentString)
  }

  @Benchmark
  def measureSmallAllParser(): Unit = {
    parser.parse(smallUserAgentString)
  }
}
