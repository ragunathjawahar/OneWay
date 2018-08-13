package io.mobsgeeks.oneway.catalogue.signup.form

interface Condition {
  fun isValid(text: String): Boolean
}
