package ua.parser

class CachingParserSpec extends ParserSpecBase {
  val parser = CachingParser.get()
}
