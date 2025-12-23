package org.uaparser.scala

import java.util.regex.Matcher

private object Util {
  def patternReplacementWithMatcherGroups(replacement: String, matcher: Matcher, agent: String): String = {
    val firstDollarIdx = replacement.indexOf('$')

    // No $ found, return the replacement as is
    if (firstDollarIdx < 0) return replacement.trim

    val groupCount  = matcher.groupCount()
    val replacementLength = replacement.length

    val stringBuilder = new java.lang.StringBuilder(replacementLength + 128)

    // Copy prefix before the first '$'
    if (firstDollarIdx > 0) stringBuilder.append(replacement, 0, firstDollarIdx)

    var i = firstDollarIdx
    while (i < replacementLength) {
      val currentChar = replacement.charAt(i)

      if (currentChar == '$' && i + 1 < replacementLength) {
        // Parse the group number
        var j = i + 1
        var group = 0
        var hasDigits = false
        var cont = true

        while (j < replacementLength && cont) {
          val d = replacement.charAt(j)
          if (d >= '0' && d <= '9') {
            hasDigits = true
            group = group * 10 + (d - '0')
            j += 1
          } else {
            cont = false
          }
        }

        if (hasDigits && group > 0) {
          // Substitute group if present + matched; otherwise insert ""
          if (group <= groupCount) {
            val start = matcher.start(group) // -1 if group did not participate
            if (start >= 0) {
              stringBuilder.append(agent, start, matcher.end(group))
            }
          }

          // skip over the digits we just processed
          i = j

        } else {
          // No digits after '$', treat it as a literal '$'
          stringBuilder.append('$')
          i += 1
        }
      } else {
        stringBuilder.append(currentChar)
        i += 1
      }
    }

    stringBuilder.toString.trim
  }
}
