package org.uaparser.scala

case class Device(family: String, brand: Option[String] = None, model: Option[String] = None)

object Device {
  private[scala] def fromMap(m: Map[String, String]) = m.get("family").map(Device(_, m.get("brand"), m.get("model")))
}
