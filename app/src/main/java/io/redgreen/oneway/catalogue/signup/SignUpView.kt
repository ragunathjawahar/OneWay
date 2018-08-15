package io.redgreen.oneway.catalogue.signup

import io.redgreen.oneway.catalogue.signup.form.PhoneNumberCondition
import io.redgreen.oneway.catalogue.signup.form.UsernameCondition

interface SignUpView {
  fun getPhoneNumber(): String
  fun showPhoneNumberErrors(unmetConditions: Set<PhoneNumberCondition>)
  fun hidePhoneNumberError()
  fun getUsername(): String
  fun showUsernameErrors(unmetConditions: Set<UsernameCondition>)
  fun hideUsernameError()
}
