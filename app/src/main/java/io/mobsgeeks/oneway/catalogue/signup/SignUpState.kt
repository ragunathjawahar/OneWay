package io.mobsgeeks.oneway.catalogue.signup

import io.mobsgeeks.oneway.catalogue.signup.form.PhoneNumberCondition

data class SignUpState(
    val phoneNumberUntouched: Boolean,
    val phoneNumberUnmetConditions: Set<PhoneNumberCondition>
) {
  companion object {
    val UNTOUCHED = SignUpState(true, emptySet())
  }

  fun unmetPhoneNumberConditions(unmetConditions: Set<PhoneNumberCondition>): SignUpState =
      copy(phoneNumberUntouched = false, phoneNumberUnmetConditions = unmetConditions)
}
