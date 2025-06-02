package org.uaparser.scala

trait UserAgentStringParser {
  def parse(agent: String): Client
}
