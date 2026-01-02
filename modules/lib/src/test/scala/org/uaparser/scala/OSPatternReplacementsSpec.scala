package org.uaparser.scala

import org.specs2.mutable.Specification

class OSPatternReplacementsSpec extends Specification {

  private def makePattern(map: Map[String, String]): OSPattern =
    OSPattern.fromMap(map).get

  "OSPattern" should {

    "expand $1 in 'os_replacement' (including multiple occurrences)" in {
      val p = makePattern(
        Map(
          "regex" -> "Foo(\\d+)",
          "os_replacement" -> "OS $1 x $1"
        )
      )

      p.process("Foo12") must beSome(OS("OS 12 x 12"))
    }

    "treat missing group 1 as empty when os_replacement contains $1" in {
      val p = makePattern(
        Map(
          "regex" -> "Foo(\\d+)?",
          "os_replacement" -> "OS$1"
        )
      )

      p.process("Foo") must beSome(OS("OS"))
    }

    "pre-parse 'os_vN_replacement' as matching group references and fall back when group is missing" in {
      val p = makePattern(
        Map(
          "regex" -> "OS (\\w+)(?: (\\d+))?(?:\\.(\\d+))?",
          "os_replacement" -> "$1",
          "os_v1_replacement" -> "$3"
        )
      )

      // group3 exists, major = 2; minor defaults to group3 => 2
      p.process("OS Android 10.2") must beSome(OS("Android", major = Some("2"), minor = Some("2")))

      // group3 missing, major falls back to group2 => 10; minor absent
      p.process("OS Android 10") must beSome(OS("Android", major = Some("10")))
    }

    "treat 'os_vN_replacement' values without group references as a literal version value" in {
      val p = makePattern(
        Map(
          "regex" -> "OS (\\w+)(?: (\\d+))?",
          "os_replacement" -> "$1",
          "os_v1_replacement" -> "11"
        )
      )

      p.process("OS iOS 17") must beSome(OS("iOS", major = Some("11")))
    }
  }
}
