package io.redgreen.oneway.catalogue.signup

import io.redgreen.oneway.catalogue.signup.form.Field
import io.redgreen.oneway.catalogue.signup.form.PhoneNumberCondition
import io.redgreen.oneway.catalogue.signup.form.UsernameCondition

data class SignUpState(
    val phoneNumberField: Field<PhoneNumberCondition>,
    val usernameField: Field<UsernameCondition>
) {
  companion object {
    val UNTOUCHED = SignUpState(Field(), Field())
  }

  fun unmetPhoneNumberConditions(unmetConditions: Set<PhoneNumberCondition>): SignUpState =
      copy(phoneNumberField = phoneNumberField.validationResult(unmetConditions))

  fun unmetUsernameConditions(unmetConditions: Set<UsernameCondition>): SignUpState =
      copy(usernameField = usernameField.validationResult(unmetConditions))

  fun displayingPhoneNumberError(displaying: Boolean): SignUpState =
      copy(phoneNumberField = phoneNumberField.copy(displayingError = displaying))

  fun displayingUsernameError(displaying: Boolean): SignUpState =
      copy(usernameField = usernameField.copy(displayingError = displaying))
}
