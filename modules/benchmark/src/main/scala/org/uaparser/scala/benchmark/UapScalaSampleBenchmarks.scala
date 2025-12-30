package org.uaparser.scala.benchmark

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole

import org.uaparser.scala.Parser

@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
class UapScalaSampleBenchmarks {
  private var parser: Parser = _
  private var uas: Array[String] = _
  private var idx: Int = 0

  @Setup(Level.Trial)
  def setup(): Unit = {
    parser = BenchmarkSupport.loadParserForPinnedRegexes()
    uas = BenchmarkSupport.loadLinesFromResource("sample-user-agents.txt")
    idx = 0
  }

  @Setup(Level.Iteration)
  def resetCursor(): Unit =
    idx = 0

  @inline private def nextUA(): String = {
    val s = uas(idx)
    idx = BenchmarkSupport.incrementIndex(idx, uas.length)
    s
  }

  @Benchmark def sample_device(bh: Blackhole): Unit =
    bh.consume(parser.deviceParser.parse(nextUA()))

  @Benchmark def sample_os(bh: Blackhole): Unit =
    bh.consume(parser.osParser.parse(nextUA()))

  @Benchmark def sample_userAgent(bh: Blackhole): Unit =
    bh.consume(parser.userAgentParser.parse(nextUA()))

  @Benchmark def sample_all(bh: Blackhole): Unit =
    bh.consume(parser.parse(nextUA()))
}
