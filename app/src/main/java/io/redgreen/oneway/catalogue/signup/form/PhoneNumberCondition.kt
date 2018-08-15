package io.redgreen.oneway.catalogue.signup.form

import java.util.regex.Pattern

enum class PhoneNumberCondition : Condition {
  LENGTH {
    override fun isValid(text: String): Boolean =
        text.length == 10
  },

  DIGITS_ONLY {
    private val digitsOnlyPattern = Pattern.compile("\\d+")

    override fun isValid(text: String): Boolean =
        digitsOnlyPattern.matcher(text).matches()
  },

  STARTS_WITH {
    private val startsWith8or9Pattern = Pattern.compile("[8,9].*", Pattern.DOTALL)

    override fun isValid(text: String): Boolean =
        startsWith8or9Pattern.matcher(text).matches()
  }
}
