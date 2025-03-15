package org.uaparser.scala.benchmark
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.CommandLineOptions


object Main {
  def main(args: Array[String]): Unit = {
    val opts = new CommandLineOptions(args*)
    val runner = new Runner(opts)
    runner.run()
  }
}
