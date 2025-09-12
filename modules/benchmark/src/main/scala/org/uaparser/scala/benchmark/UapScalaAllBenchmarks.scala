package org.uaparser.scala.benchmark

import scala.io.Source

import org.openjdk.jmh.annotations.{Benchmark, Scope, State}
import org.openjdk.jmh.infra.Blackhole

import org.uaparser.scala.Parser

@State(Scope.Benchmark)
class UapScalaAllBenchmarks {

  // an entire bundle of strings taken from the current suite of tests
  val allUserAgentStrings: List[String] = Source.fromResource("all-user-agents.txt").getLines().toList

  var parser: Parser =
    Parser.fromInputStream(Thread.currentThread.getContextClassLoader.getResourceAsStream("regexes_@7388149c.yaml")).get

  @Benchmark
  def measureAllStrDeviceParser(bh: Blackhole): Unit =
    allUserAgentStrings.foreach(s => bh.consume(parser.deviceParser.parse(s)))

  @Benchmark
  def measureAllStrOsParser(bh: Blackhole): Unit =
    allUserAgentStrings.foreach(s => bh.consume(parser.osParser.parse(s)))

  @Benchmark
  def measureAllStrUserAgentParser(bh: Blackhole): Unit =
    allUserAgentStrings.foreach(s => bh.consume(parser.userAgentParser.parse(s)))

  @Benchmark
  def measureAllStrAllParser(bh: Blackhole): Unit =
    allUserAgentStrings.foreach(s => bh.consume(parser.parse(s)))
}
