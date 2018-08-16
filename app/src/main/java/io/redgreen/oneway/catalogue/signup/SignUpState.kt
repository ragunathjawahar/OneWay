package io.redgreen.oneway.catalogue.signup

import android.os.Parcelable
import io.redgreen.oneway.catalogue.signup.form.Field
import io.redgreen.oneway.catalogue.signup.form.PhoneNumberCondition
import io.redgreen.oneway.catalogue.signup.form.UsernameCondition
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SignUpState(
    val phoneNumberField: Field<PhoneNumberCondition>,
    val usernameField: Field<UsernameCondition>
) : Parcelable {
  companion object {
    val UNTOUCHED = SignUpState(Field(), Field())
  }

  fun phoneNumberUnmetConditions(unmetConditions: Set<PhoneNumberCondition>): SignUpState =
      copy(phoneNumberField = phoneNumberField.unmetConditions(unmetConditions))

  fun phoneNumberDisplayError(display: Boolean): SignUpState =
      copy(phoneNumberField = phoneNumberField.displayError(display))

  fun usernameUnmetConditions(unmetConditions: Set<UsernameCondition>): SignUpState =
      copy(usernameField = usernameField.unmetConditions(unmetConditions))

  fun usernameDisplayError(displaying: Boolean): SignUpState =
      copy(usernameField = usernameField.displayError(displaying))
}
