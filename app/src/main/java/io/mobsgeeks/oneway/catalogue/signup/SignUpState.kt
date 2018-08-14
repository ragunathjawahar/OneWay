package io.mobsgeeks.oneway.catalogue.signup

import io.mobsgeeks.oneway.catalogue.signup.form.PhoneNumber

data class SignUpState(
    val phoneNumberUntouched: Boolean,
    val phoneNumberUnmetConditions: Set<PhoneNumber>
) {
  companion object {
    val UNTOUCHED = SignUpState(true, emptySet())
  }

  fun unmetPhoneNumberConditions(unmetConditions: Set<PhoneNumber>): SignUpState =
      copy(phoneNumberUntouched = false, phoneNumberUnmetConditions = unmetConditions)
}
