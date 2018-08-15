package io.mobsgeeks.oneway.catalogue.signup

import io.mobsgeeks.oneway.catalogue.signup.form.PhoneNumberCondition
import io.mobsgeeks.oneway.catalogue.signup.form.UsernameCondition

interface SignUpView {
  fun showPhoneNumberErrors(unmetConditions: Set<PhoneNumberCondition>)
  fun hidePhoneNumberError()
  fun showUsernameErrors(unmetConditions: Set<UsernameCondition>)
  fun hideUsernameError()
}
