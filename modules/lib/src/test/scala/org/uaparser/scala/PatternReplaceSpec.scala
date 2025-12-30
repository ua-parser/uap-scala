package org.uaparser.scala

import java.util.regex.Pattern

import org.specs2.mutable.Specification

class PatternReplaceSpec extends Specification {

  // Helper: invoke the private `replace` method reflectively
  private def replace(replacement: String, agent: String, p: Pattern): String = {
    val m = p.matcher(agent)
    m.find() must beTrue
    Util.patternReplacementWithMatcherGroups(replacement, m, agent)
  }

  "patternReplacementWithMatcherGroups" should {

    "return trimmed replacement when there is no '$'" in {
      replace("  Foo Bar  ", "anything", Pattern.compile(".*")) must beEqualTo("Foo Bar")
    }

    "treat '$' not followed by digits as a literal '$'" in {
      replace("price$USD", "anything", Pattern.compile(".*")) must beEqualTo("price$USD")
    }

    "replace $1 with group 1 (and replace all occurrences)" in {
      val p = Pattern.compile("Samsung ([^ ]+)")
      replace("$1 $1", "Samsung SM-G991B", p) must beEqualTo("SM-G991B SM-G991B")
    }

    "support multi-digit group references like $10" in {
      val p = Pattern.compile("(a)(b)(c)(d)(e)(f)(g)(h)(i)(j)")
      replace("$10-$1", "abcdefghij", p) must beEqualTo("j-a")
    }

    "expand missing or non-participating groups to empty string" in {
      val p = Pattern.compile("foo(\\d+)?")
      replace("x$1y", "foo", p) must beEqualTo("xy")
    }

    "expand out-of-range groups to empty string" in {
      val p = Pattern.compile("foo(\\d+)?")
      replace("x$2y", "foo", p) must beEqualTo("xy")
    }

    "ignore index 0" in {
      val p = Pattern.compile("(X)")
      replace("$0-$1", "X", p) must beEqualTo("$0-X")
    }

    "preserve a trailing '$' literally" in {
      replace("foo$", "anything", Pattern.compile(".*")) must beEqualTo("foo$")
    }
  }
}
