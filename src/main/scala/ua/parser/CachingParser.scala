package ua.parser

import java.io.InputStream

import com.twitter.util.LruMap

case class CachingParser(parser: Parser, size: Int) extends UserAgentStringParser {
  lazy val clients = new LruMap[String, Client](size)
  def parse(agent: String) = clients.get(agent).getOrElse {
    val client = parser.parse(agent)
    clients.put(agent, client)
    client
  }
}

object CachingParser {
  val defaultCacheSize = 1000
  def create(source: InputStream, size: Int = defaultCacheSize) = CachingParser(Parser.create(source), size)
  def get(size: Int = defaultCacheSize) = CachingParser(Parser.get, size)
}
