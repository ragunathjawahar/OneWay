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

  fun unmetPhoneNumberConditions(unmetConditions: Set<PhoneNumberCondition>): SignUpState =
      copy(phoneNumberField = phoneNumberField.validationResult(unmetConditions))

  fun unmetUsernameConditions(unmetConditions: Set<UsernameCondition>): SignUpState =
      copy(usernameField = usernameField.validationResult(unmetConditions))

  fun displayingPhoneNumberError(displaying: Boolean): SignUpState =
      copy(phoneNumberField = phoneNumberField.displayError(displaying))

  fun displayingUsernameError(displaying: Boolean): SignUpState =
      copy(usernameField = usernameField.displayError(displaying))

  fun displayPhoneNumberErrorImmediate(unmetConditions: Set<PhoneNumberCondition>): SignUpState =
      copy(phoneNumberField = phoneNumberField.validationResult(unmetConditions, true))

  fun displayUsernameErrorImmediate(unmetConditions: Set<UsernameCondition>): SignUpState =
      copy(usernameField = usernameField.validationResult(unmetConditions, true))
}
