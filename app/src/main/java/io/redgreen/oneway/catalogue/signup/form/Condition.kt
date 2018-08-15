package io.redgreen.oneway.catalogue.signup.form

interface Condition {
  fun isValid(text: String): Boolean
}
