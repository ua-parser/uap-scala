package org.uaparser.scala

import java.io.InputStream
import java.util
import java.util.{Collections, Map as JMap}

import scala.util.Try

case class CachingParser(parser: Parser, maxEntries: Int) extends UserAgentStringParser {
  lazy val clients: JMap[String, Client] = Collections.synchronizedMap(
    new util.LinkedHashMap[String, Client](maxEntries + 1, 1.0f, true) {
      override protected def removeEldestEntry(eldest: JMap.Entry[String, Client]): Boolean =
        super.size > maxEntries
    }
  )
  def parse(agent: String): Client = Option(clients.get(agent)).getOrElse {
    val client = parser.parse(agent)
    clients.put(agent, client)
    client
  }
}

object CachingParser {
  private val defaultCacheSize: Int = 1000
  def fromInputStream(source: InputStream, size: Int = defaultCacheSize): Try[CachingParser] =
    Parser.fromInputStream(source).map(CachingParser(_, size))
  def default(size: Int = defaultCacheSize): CachingParser = CachingParser(Parser.default, size)
}
