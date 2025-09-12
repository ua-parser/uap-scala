package org.uaparser.scala.benchmark

import scala.io.Source

import org.openjdk.jmh.annotations.{Benchmark, Scope, State}
import org.openjdk.jmh.infra.Blackhole

import org.uaparser.scala.Parser

@State(Scope.Benchmark)
class UapScalaSampleBenchmarks {

  // an entire bundle of strings taken from the current suite of tests
  val sampleUserAgentStrings: List[String] = Source.fromResource("sample-user-agents.txt").getLines().toList

  var parser: Parser =
    Parser.fromInputStream(Thread.currentThread.getContextClassLoader.getResourceAsStream("regexes_@7388149c.yaml")).get

  @Benchmark
  def measureSampleStrDeviceParser(bh: Blackhole): Unit =
    sampleUserAgentStrings.foreach(s => bh.consume(parser.deviceParser.parse(s)))

  @Benchmark
  def measureSampleStrOsParser(bh: Blackhole): Unit =
    sampleUserAgentStrings.foreach(s => bh.consume(parser.osParser.parse(s)))

  @Benchmark
  def measureSampleStrUserAgentParser(bh: Blackhole): Unit =
    sampleUserAgentStrings.foreach(s => bh.consume(parser.userAgentParser.parse(s)))

  @Benchmark
  def measureSampleStrAllParser(bh: Blackhole): Unit =
    sampleUserAgentStrings.foreach(s => bh.consume(parser.parse(s)))
}
