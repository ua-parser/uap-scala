package org.uaparser.scala

import java.io.InputStream

class CachingParserSpec extends ParserSpecBase {
  val parser = CachingParser.get()
  def createFromStream(stream: InputStream): UserAgentStringParser = CachingParser.create(stream)
}
