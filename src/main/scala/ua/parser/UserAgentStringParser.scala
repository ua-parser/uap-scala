package ua.parser

trait UserAgentStringParser {
  def parse(agent: String): Client
}
