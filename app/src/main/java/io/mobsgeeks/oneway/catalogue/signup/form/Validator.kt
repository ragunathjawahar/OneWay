package io.mobsgeeks.oneway.catalogue.signup.form

class Validator {
  inline fun <reified T> validate(text: String): Set<T> where T : Enum<T>, T : Condition =
      enumValues<T>().filter { !it.isValid(text) }.toSet()
}
