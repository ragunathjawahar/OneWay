package io.mobsgeeks.oneway.catalogue.signup.form

import java.util.regex.Pattern

enum class Username : Condition {
  ALLOWED_CHARACTERS {
    private val alphabetPattern = Pattern.compile("\\w+")

    override fun isValid(text: String): Boolean =
        alphabetPattern.matcher(text).matches()
  },

  MIN_LENGTH {
    private val minLength = 3

    override fun isValid(text: String): Boolean =
        text.length >= minLength
  },

  NO_SPACES {
    private val whitespacePattern = Pattern.compile("\\s")

    override fun isValid(text: String): Boolean =
        !whitespacePattern.matcher(text).find()
  },

  STARTS_WITH_ALPHABET {
    override fun isValid(text: String): Boolean =
        text.isNotEmpty() && text[0].isLetter()
  }
}
