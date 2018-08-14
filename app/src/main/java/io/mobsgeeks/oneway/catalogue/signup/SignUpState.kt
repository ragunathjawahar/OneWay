package io.mobsgeeks.oneway.catalogue.signup

import io.mobsgeeks.oneway.catalogue.signup.form.PhoneNumberCondition
import io.mobsgeeks.oneway.catalogue.signup.form.UsernameCondition

data class SignUpState(
    val phoneNumberUntouched: Boolean,
    val phoneNumberUnmetConditions: Set<PhoneNumberCondition>,
    val usernameUntouched: Boolean,
    val usernameUnmetConditions: Set<UsernameCondition>
) {
  companion object {
    val UNTOUCHED = SignUpState(true, emptySet(), true, emptySet())
  }

  fun unmetPhoneNumberConditions(unmetConditions: Set<PhoneNumberCondition>): SignUpState =
      copy(phoneNumberUntouched = false, phoneNumberUnmetConditions = unmetConditions)

  fun unmetUsernameConditions(unmetConditions: Set<UsernameCondition>): SignUpState =
      copy(usernameUntouched = false, usernameUnmetConditions = unmetConditions)
}
