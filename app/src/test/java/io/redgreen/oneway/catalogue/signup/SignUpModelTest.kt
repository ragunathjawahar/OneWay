package io.redgreen.oneway.catalogue.signup

import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.catalogue.signup.SignUpState.Companion.UNTOUCHED
import io.redgreen.oneway.catalogue.signup.drivers.DisplayErrorEvent
import io.redgreen.oneway.catalogue.signup.form.PhoneNumberCondition
import io.redgreen.oneway.catalogue.signup.form.PhoneNumberCondition.LENGTH
import io.redgreen.oneway.catalogue.signup.form.UsernameCondition
import io.redgreen.oneway.catalogue.signup.form.UsernameCondition.MIN_LENGTH
import io.redgreen.oneway.catalogue.signup.form.Validator
import io.redgreen.oneway.catalogue.signup.form.WhichField.PHONE_NUMBER
import io.redgreen.oneway.catalogue.signup.form.WhichField.USERNAME
import io.redgreen.oneway.catalogue.signup.usecases.SignUpUseCases
import io.redgreen.oneway.test.MviTestRule
import org.junit.Test

class SignUpModelTest {
  private val intentionsSubject = PublishSubject.create<SignUpIntention>()
  private val displayErrorEventsSubject = PublishSubject.create<DisplayErrorEvent>()
  private val validator = Validator()

  private val testRule = MviTestRule<SignUpState> { sourceEvents, timeline ->
    SignUpModel.createSource(
        intentionsSubject,
        displayErrorEventsSubject,
        sourceEvents,
        SignUpUseCases(UNTOUCHED, timeline, validator)
    )
  }

  @Test fun `when the screen is created, then the fields are untouched`() {
    // when
    testRule.sourceIsCreated()

    // then
    testRule.assertStates(UNTOUCHED)
  }

  @Test fun `when the screen is restored, then the last known state is restored`() {
    // given
    val validPhoneNumberState = UNTOUCHED.phoneNumberUnmetConditions(emptySet())
    testRule.startWith(validPhoneNumberState) {}
    testRule.sourceIsDestroyed()

    // when
    testRule.sourceIsRestored()

    // then
    testRule.assertStates(validPhoneNumberState)
  }

  @Test fun `when a valid phone number is entered, then there are no unmet conditions`() {
    // given
    val validPhoneNumber = "9876543210"

    // when
    testRule.startWith(UNTOUCHED) {
      typePhoneNumber(validPhoneNumber)
    }

    // then
    val validPhoneNumberState = UNTOUCHED.phoneNumberUnmetConditions(emptySet())
    testRule.assertStates(validPhoneNumberState)
  }

  @Test fun `when an invalid phone number is entered, then the field has unmet conditions`() {
    // given
    val invalidPhoneNumber = ""

    // when
    testRule.startWith(UNTOUCHED) {
      typePhoneNumber(invalidPhoneNumber)
    }

    // then
    val invalidPhoneNumberState = UNTOUCHED
        .phoneNumberUnmetConditions(PhoneNumberCondition.values().toSet())
    testRule.assertStates(invalidPhoneNumberState)
  }

  @Test fun `when a valid username is entered, then there are no unmet conditions`() {
    // given
    val validUsername = "spiderman"

    // when
    testRule.startWith(UNTOUCHED) {
      typeUsername(validUsername)
    }

    // then
    val validUsernameState = UNTOUCHED.usernameUnmetConditions(emptySet())
    testRule.assertStates(validUsernameState)
  }

  @Test fun `when an invalid username is entered, then the field has unmet conditions`() {
    // given
    val invalidUsername = " "

    // when
    testRule.startWith(UNTOUCHED) {
      typeUsername(invalidUsername)
    }

    // then
    val invalidUsernameState = UNTOUCHED
        .usernameUnmetConditions(UsernameCondition.values().toSet())
    testRule.assertStates(invalidUsernameState)
  }

  @Test fun `when phone number has an error and error threshold elapses, then show error immediately`() {
    // given
    val invalidPhoneNumberState = UNTOUCHED
        .phoneNumberUnmetConditions(setOf(LENGTH))

    // when
    testRule.startWith(invalidPhoneNumberState) {
      displayErrorEventsSubject.onNext(DisplayErrorEvent(PHONE_NUMBER, true))
    }

    // then
    val displayingPhoneNumberErrorState = invalidPhoneNumberState
        .phoneNumberDisplayError(true)
    testRule.assertStates(displayingPhoneNumberErrorState)
  }

  @Test fun `when phone number displays an error but becomes valid, then hide error immediately`() {
    // given
    val invalidPhoneNumberState = UNTOUCHED
        .phoneNumberUnmetConditions(setOf(LENGTH))

    // when
    testRule.startWith(invalidPhoneNumberState) {
      typePhoneNumber("9876543210")
      displayErrorEventsSubject.onNext(DisplayErrorEvent(PHONE_NUMBER, false))
    }

    // then
    val validPhoneNumberState = invalidPhoneNumberState
        .phoneNumberUnmetConditions(emptySet())
    val notDisplayingPhoneNumberErrorState = validPhoneNumberState
        .phoneNumberDisplayError(false)

    testRule.assertStates(
        validPhoneNumberState,
        notDisplayingPhoneNumberErrorState
    )
  }

  @Test fun `when username has an error and error threshold elapses, then show error immediately`() {
    // given
    val invalidUsernameState = UNTOUCHED
        .usernameUnmetConditions(setOf(MIN_LENGTH))

    // when
    testRule.startWith(invalidUsernameState) {
      displayErrorEventsSubject.onNext(DisplayErrorEvent(USERNAME, true))
    }

    // then
    val displayingUsernameErrorState = invalidUsernameState
        .usernameDisplayError(true)
    testRule.assertStates(displayingUsernameErrorState)
  }

  @Test fun `when username displays an error but becomes valid, then hide error immediately`() {
    // given
    val invalidUsernameState = UNTOUCHED
        .usernameUnmetConditions(setOf(MIN_LENGTH))

    // when
    testRule.startWith(invalidUsernameState) {
      typeUsername("groot")
      displayErrorEventsSubject.onNext(DisplayErrorEvent(USERNAME, false))
    }

    // then
    val validUsernameState = invalidUsernameState
        .usernameUnmetConditions(emptySet())
    val notDisplayingUsernameErrorState = validUsernameState
        .usernameDisplayError(false)

    testRule.assertStates(
        validUsernameState,
        notDisplayingUsernameErrorState
    )
  }

  @Test fun `when phone number loses focus, then validate and show validation result immediately`() {
    // given
    val phoneNumber = ""
    val unmetConditions = validator.validate<PhoneNumberCondition>(phoneNumber)
    val displayPhoneNumberErrorImmediateState = UNTOUCHED
        .phoneNumberUnmetConditions(unmetConditions)
        .phoneNumberDisplayError(true)

    // when
    testRule.startWith(UNTOUCHED) {
      loseFocusPhoneNumber(phoneNumber)
    }

    // then
    testRule.assertStates(displayPhoneNumberErrorImmediateState)
  }

  @Test fun `when username loses focus, then validate and show validation result immediately`() {
    // given
    val username = ""
    val unmetConditions = validator.validate<UsernameCondition>(username)
    val displayUsernameErrorImmediateState = UNTOUCHED
        .usernameUnmetConditions(unmetConditions)
        .usernameDisplayError(true)

    // when
    testRule.startWith(UNTOUCHED) {
      loseFocusUsername(username)
    }

    // then
    testRule.assertStates(displayUsernameErrorImmediateState)
  }

  @Test fun `when sign up clicked, then validate all fields immediately`() {
    // given
    val phoneNumber = ""
    val username = ""
    val phoneNumberUnmetConditions = validator.validate<PhoneNumberCondition>(phoneNumber)
    val usernameUnmetConditions = validator.validate<UsernameCondition>(username)

    // when
    testRule.startWith(UNTOUCHED) {
      tapSignUp(phoneNumber, username)
    }

    // then
    val validatedAllFieldsState = UNTOUCHED
        .phoneNumberUnmetConditions(phoneNumberUnmetConditions)
        .phoneNumberDisplayError(true)
        .usernameUnmetConditions(usernameUnmetConditions)
        .usernameDisplayError(true)
    testRule.assertStates(validatedAllFieldsState)
  }

  private fun typePhoneNumber(phoneNumber: String) {
    intentionsSubject.onNext(EnterInputIntention.phoneNumber(phoneNumber))
  }

  private fun typeUsername(username: String) {
    intentionsSubject.onNext(EnterInputIntention.username(username))
  }

  private fun loseFocusPhoneNumber(phoneNumber: String) {
    intentionsSubject.onNext(LoseFocusIntention.phoneNumber(phoneNumber))
  }

  private fun loseFocusUsername(username: String) {
    intentionsSubject.onNext(LoseFocusIntention.username(username))
  }

  private fun tapSignUp(phoneNumber: String, username: String) {
    intentionsSubject.onNext(SignUpCtaIntention(phoneNumber, username))
  }
}
