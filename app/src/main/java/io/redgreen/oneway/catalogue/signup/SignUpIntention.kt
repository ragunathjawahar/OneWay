package io.redgreen.oneway.catalogue.signup

import io.redgreen.oneway.catalogue.signup.form.WhichField
import io.redgreen.oneway.catalogue.signup.form.WhichField.PHONE_NUMBER
import io.redgreen.oneway.catalogue.signup.form.WhichField.USERNAME

sealed class SignUpIntention

data class EnterInputIntention(
    val text: String,
    val whichField: WhichField
) : SignUpIntention() {
  companion object {
    fun phoneNumber(phoneNumber: String): EnterInputIntention =
        EnterInputIntention(phoneNumber, PHONE_NUMBER)

    fun username(username: String): EnterInputIntention =
        EnterInputIntention(username, USERNAME)
  }
}

data class LoseFocusIntention(
    val text: String,
    val whichField: WhichField
) : SignUpIntention() {
  companion object {
    fun phoneNumber(phoneNumber: String): LoseFocusIntention =
        LoseFocusIntention(phoneNumber, PHONE_NUMBER)

    fun username(username: String): LoseFocusIntention =
        LoseFocusIntention(username, USERNAME)
  }
}
