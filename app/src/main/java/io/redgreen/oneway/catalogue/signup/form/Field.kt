package io.redgreen.oneway.catalogue.signup.form

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Field<T>(
    val untouched: Boolean = true,
    val unmetConditions: Set<T> = emptySet(),
    val displayingError: Boolean = false
) : Parcelable where T : Enum<T>, T : Condition {
  fun validationResult(unmetConditions: Set<T>): Field<T> =
      copy(untouched = false, unmetConditions = unmetConditions)

  fun validationResult(unmetConditions: Set<T>, displayError: Boolean): Field<T> =
      copy(untouched = false, unmetConditions = unmetConditions, displayingError = displayError)

  fun displayError(display: Boolean): Field<T> =
      copy(displayingError = display)
}
