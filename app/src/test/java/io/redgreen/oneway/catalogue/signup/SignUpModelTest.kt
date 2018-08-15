package io.redgreen.oneway.catalogue.signup

import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.catalogue.signup.SignUpState.Companion.UNTOUCHED
import io.redgreen.oneway.catalogue.signup.drivers.DisplayErrorEvent
import io.redgreen.oneway.catalogue.signup.drivers.DisplayPhoneNumberErrorEvent
import io.redgreen.oneway.catalogue.signup.drivers.DisplayUsernameErrorEvent
import io.redgreen.oneway.catalogue.signup.form.PhoneNumberCondition
import io.redgreen.oneway.catalogue.signup.form.PhoneNumberCondition.LENGTH
import io.redgreen.oneway.catalogue.signup.form.UsernameCondition
import io.redgreen.oneway.catalogue.signup.form.UsernameCondition.MIN_LENGTH
import io.redgreen.oneway.catalogue.signup.form.Validator
import io.redgreen.oneway.catalogue.signup.usecases.SignUpUseCases
import io.redgreen.oneway.test.MviTestRule
import org.junit.Test

class SignUpModelTest {
  private val intentionsSubject = PublishSubject.create<SignUpIntention>()
  private val displayErrorEventsSubject = PublishSubject.create<DisplayErrorEvent>()

  private val testRule = MviTestRule<SignUpState> { sourceEvents, timeline ->
    val validator = Validator()

    SignUpModel.createSource(
        intentionsSubject,
        displayErrorEventsSubject,
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

  @Test fun `when view displays an error for phone number, update phone number field as such`() {
    // given
    val invalidPhoneNumberState = UNTOUCHED
        .unmetPhoneNumberConditions(setOf(LENGTH))

    // when
    testRule.startWith(invalidPhoneNumberState) {
      displayErrorEventsSubject.onNext(DisplayPhoneNumberErrorEvent(true))
    }

    // then
    val displayingPhoneNumberErrorState = invalidPhoneNumberState
        .displayingPhoneNumberError(true)
    testRule.assertStates(displayingPhoneNumberErrorState)
  }

  @Test fun `when view stops displaying error for phone number, update phone number field as such`() {
    // given
    val invalidPhoneNumberState = UNTOUCHED
        .unmetPhoneNumberConditions(setOf(LENGTH))

    // when
    testRule.startWith(invalidPhoneNumberState) {
      typePhoneNumber("9876543210")
      displayErrorEventsSubject.onNext(DisplayPhoneNumberErrorEvent(false))
    }

    // then
    val validPhoneNumberState = invalidPhoneNumberState
        .unmetPhoneNumberConditions(emptySet())
    val notDisplayingPhoneNumberErrorState = validPhoneNumberState
        .displayingPhoneNumberError(false)

    testRule.assertStates(
        validPhoneNumberState,
        notDisplayingPhoneNumberErrorState
    )
  }

  @Test fun `when view displays an error for username, update username field as such`() {
    // given
    val invalidUsernameState = UNTOUCHED
        .unmetUsernameConditions(setOf(MIN_LENGTH))

    // when
    testRule.startWith(invalidUsernameState) {
      displayErrorEventsSubject.onNext(DisplayUsernameErrorEvent(true))
    }

    // then
    val displayingUsernameErrorState = invalidUsernameState
        .displayingUsernameError(true)
    testRule.assertStates(displayingUsernameErrorState)
  }

  @Test fun `when view stops displaying error for username, update username field as such`() {
    // given
    val invalidUsernameState = UNTOUCHED
        .unmetUsernameConditions(setOf(MIN_LENGTH))

    // when
    testRule.startWith(invalidUsernameState) {
      typeUsername("groot")
      displayErrorEventsSubject.onNext(DisplayUsernameErrorEvent(false))
    }

    // then
    val validUsernameState = invalidUsernameState
        .unmetUsernameConditions(emptySet())
    val notDisplayingUsernameErrorState = validUsernameState
        .displayingUsernameError(false)

    testRule.assertStates(
        validUsernameState,
        notDisplayingUsernameErrorState
    )
  }

  private fun typePhoneNumber(phoneNumber: String) {
    intentionsSubject.onNext(EnterInputIntention.phoneNumber(phoneNumber))
  }

  private fun typeUsername(username: String) {
    intentionsSubject.onNext(EnterInputIntention.username(username))
  }
}
