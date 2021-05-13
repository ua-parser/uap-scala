package org.uaparser.scala

import java.io.InputStream

class ParserSpec extends ParserSpecBase {
  val parser = Parser.default
  def createFromStream(stream: InputStream): UserAgentStringParser = Parser.fromInputStream(stream).get

  "parParse should works well" >> {
    import java.util.Date
    val exampleUaStr = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
      "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36"
    val exampleList = (0 to 7000).map(e => exampleUaStr)

    var startTime = new Date().getTime
    val resultListSingleThread = exampleList.map(parser.parse)
    var endTime = new Date().getTime
    print(s"Single thread parsing costs ${endTime - startTime} millisecond(s)\n")

    startTime = new Date().getTime
    val resultListMultithreading = exampleList.map(parser.parParse)
    endTime = new Date().getTime
    print(s"Multithreading parsing costs ${endTime - startTime} millisecond(s)\n")

    resultListSingleThread must beEqualTo(resultListMultithreading)
  }
}
