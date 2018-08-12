package io.mobsgeeks.oneway.catalogue.signup

interface Condition {
  fun isValid(text: String): Boolean
}
