package org.uaparser.scala

import java.io.InputStream

class ParserSpec extends ParserSpecBase {
  val parser = Parser.get
  def createFromStream(stream: InputStream): UserAgentStringParser = Parser.create(stream)
}
