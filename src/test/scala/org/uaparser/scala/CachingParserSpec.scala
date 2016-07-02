package org.uaparser.scala

class CachingParserSpec extends ParserSpecBase {
  val parser = CachingParser.get()
}
