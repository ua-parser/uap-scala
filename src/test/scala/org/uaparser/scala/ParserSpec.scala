package org.uaparser.scala

import java.io.InputStream

class ParserSpec extends ParserSpecBase {
  val parser = Parser.default
  def createFromStream(stream: InputStream): UserAgentStringParser = Parser.fromInputStream(stream).get
}
