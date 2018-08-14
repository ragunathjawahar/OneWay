package io.mobsgeeks.oneway.catalogue.signup

import io.mobsgeeks.oneway.catalogue.signup.SignUpState.Companion.UNTOUCHED
import io.mobsgeeks.oneway.catalogue.signup.form.PhoneNumberCondition
import io.mobsgeeks.oneway.catalogue.signup.form.Validator
import io.mobsgeeks.oneway.catalogue.signup.usecases.SignUpUseCases
import io.mobsgeeks.oneway.test.MviTestRule
import io.reactivex.subjects.PublishSubject
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

  private fun typePhoneNumber(phoneNumber: String) {
    intentionsSubject.onNext(EnterPhoneNumberIntention(phoneNumber))
  }

  private fun typeUsername(username: String) {
    intentionsSubject.onNext(EnterUsernameIntention(username))
  }
}
