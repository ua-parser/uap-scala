package org.uaparser.scala

case class OS(
    family: String,
    major: Option[String] = None,
    minor: Option[String] = None,
    patch: Option[String] = None,
    patchMinor: Option[String] = None
)

object OS {
  private[scala] def fromMap(m: Map[String, String]) = m.get("family").map { family =>
    OS(family, m.get("major"), m.get("minor"), m.get("patch"), m.get("patch_minor"))
  }
}
