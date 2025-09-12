package org.uaparser.scala

case class UserAgent(
    family: String,
    major: Option[String] = None,
    minor: Option[String] = None,
    patch: Option[String] = None
)

object UserAgent {
  private[scala] def fromMap(m: Map[String, String]) = m.get("family").map { family =>
    UserAgent(family, m.get("major"), m.get("minor"), m.get("patch"))
  }
}
