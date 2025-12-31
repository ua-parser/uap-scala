package org.uaparser.scala.benchmark

import scala.io.Source

import org.uaparser.scala.Parser

object BenchmarkSupport {

  def loadLinesFromResource(resourceName: String): Array[String] = {
    val source = Source.fromResource(resourceName)
    try
      source.getLines().map(_.trim).filter(_.nonEmpty).toArray
    finally
      source.close()
  }

  def loadParserForPinnedRegexes(): Parser = {
    val inputStream = getClass.getClassLoader.getResourceAsStream("regexes_@354aebe.yaml")
    try
      Parser.fromInputStream(inputStream).get
    finally
      inputStream.close()
  }

  @inline def incrementIndex(i: Int, n: Int): Int = {
    val j = i + 1
    if (j == n) 0 else j
  }
}
