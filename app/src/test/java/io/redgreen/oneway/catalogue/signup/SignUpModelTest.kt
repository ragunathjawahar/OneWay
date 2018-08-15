package io.redgreen.oneway.catalogue.signup

import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.catalogue.signup.SignUpState.Companion.UNTOUCHED
import io.redgreen.oneway.catalogue.signup.form.PhoneNumberCondition
import io.redgreen.oneway.catalogue.signup.form.UsernameCondition
import io.redgreen.oneway.catalogue.signup.form.Validator
import io.redgreen.oneway.catalogue.signup.usecases.SignUpUseCases
import io.redgreen.oneway.test.MviTestRule
import org.junit.Test

class SignUpModelTest {
  private val intentionsSubject = PublishSubject.create<SignUpIntention>()

  private val testRule = MviTestRule<SignUpState> { sourceEvents, timeline ->
    val validator = Validator()

    SignUpModel.createSource(
        intentionsSubject,
        sourceEvents,
        SignUpUseCases(UNTOUCHED, timeline, validator)
    )
  }

  @Test fun `creating the screen starts with an untouched state`() {
    // when
    testRule.sourceIsCreated()

    // then
    testRule.assertStates(UNTOUCHED)
  }

  @Test fun `restoring the screen restores with the last known state`() {
    // given
    val validPhoneNumberState = UNTOUCHED.unmetPhoneNumberConditions(emptySet())
    testRule.startWith(validPhoneNumberState) {}
    testRule.sourceIsDestroyed()

    // when
    testRule.sourceIsRestored()

    // then
    testRule.assertStates(validPhoneNumberState)
  }

  @Test fun `entering a valid phone number validates the phone number`() {
    // given
    val validPhoneNumber = "9876543210"

    // when
    testRule.startWith(UNTOUCHED) {
      typePhoneNumber(validPhoneNumber)
    }

    // then
    val validPhoneNumberState = UNTOUCHED.unmetPhoneNumberConditions(emptySet())
    testRule.assertStates(validPhoneNumberState)
  }

  @Test fun `entering an invalid phone number validates the phone number`() {
    // given
    val invalidPhoneNumber = ""

    // when
    testRule.startWith(UNTOUCHED) {
      typePhoneNumber(invalidPhoneNumber)
    }

    // then
    val invalidPhoneNumberState = UNTOUCHED
        .unmetPhoneNumberConditions(PhoneNumberCondition.values().toSet())
    testRule.assertStates(invalidPhoneNumberState)
  }

  @Test fun `entering a valid username validates the username`() {
    // given
    val validUsername = "spiderman"

    // when
    testRule.startWith(UNTOUCHED) {
      typeUsername(validUsername)
    }

    // then
    val validUsernameState = UNTOUCHED.unmetUsernameConditions(emptySet())
    testRule.assertStates(validUsernameState)
  }

  @Test fun `entering an invalid username validates the username`() {
    // given
    val invalidUsername = " "

    // when
    testRule.startWith(UNTOUCHED) {
      typeUsername(invalidUsername)
    }

    // then
    val invalidUsernameState = UNTOUCHED
        .unmetUsernameConditions(UsernameCondition.values().toSet())
    testRule.assertStates(invalidUsernameState)
  }

  private fun typePhoneNumber(phoneNumber: String) {
    intentionsSubject.onNext(EnterPhoneNumberIntention(phoneNumber))
  }

  private fun typeUsername(username: String) {
    intentionsSubject.onNext(EnterUsernameIntention(username))
  }
}
