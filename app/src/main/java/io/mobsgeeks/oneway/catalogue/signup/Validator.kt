package io.mobsgeeks.oneway.catalogue.signup

class Validator(private vararg val conditions: Username) {
  fun validate(username: String): Set<Username> =
      conditions.filter { !it.isValid(username) }.toSet()
}
