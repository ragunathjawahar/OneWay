package io.mobsgeeks.oneway.catalogue.signup.form

data class Field<T>(
    val untouched: Boolean = false,
    val unmetConditions: Set<T> = emptySet()
) where T : Enum<T>, T : Condition {
  fun validationResult(unmetConditions: Set<T>): Field<T> =
      copy(untouched = false, unmetConditions = unmetConditions)
}
