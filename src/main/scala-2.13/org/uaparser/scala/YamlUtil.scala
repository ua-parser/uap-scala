package org.uaparser.scala

import org.yaml.snakeyaml.{LoaderOptions, Yaml}
import org.yaml.snakeyaml.constructor.SafeConstructor
import java.io.InputStream
import scala.jdk.CollectionConverters._
import org.yaml.snakeyaml.{LoaderOptions, Yaml}
import org.yaml.snakeyaml.constructor.SafeConstructor
import java.util.{List => JList, Map => JMap}

private[scala] object YamlUtil {

  def loadYamlAsMap(yamlStream: InputStream, loader: Yaml): Map[String, List[Map[String, String]]] = {
    val javaConfig = loader.load[JMap[String, JList[JMap[String, String]]]](yamlStream)
    javaConfig.asScala.map { case (k, v) =>
      k -> v.asScala.map(_.asScala.filter{case (_, v) => v != null}.toMap).toList
    }.toMap
  }

  def loadYamlAsMap(yamlStream: InputStream): Map[String, List[Map[String, String]]] = {
    loadYamlAsMap(yamlStream, new Yaml(new SafeConstructor(new LoaderOptions)))
  }
}
