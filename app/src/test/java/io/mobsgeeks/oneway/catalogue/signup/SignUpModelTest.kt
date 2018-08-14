package io.mobsgeeks.oneway.catalogue.signup

import io.mobsgeeks.oneway.catalogue.signup.SignUpState.Companion.UNTOUCHED
import io.mobsgeeks.oneway.catalogue.signup.form.PhoneNumber
import io.mobsgeeks.oneway.catalogue.signup.form.Validator
import io.mobsgeeks.oneway.catalogue.signup.usecases.ValidatePhoneNumberUseCase
import io.mobsgeeks.oneway.test.MviTestRule
import io.mobsgeeks.oneway.usecases.SourceCreatedUseCase
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class SignUpModelTest {
  private val intentionsSubject = PublishSubject.create<SignUpIntention>()

  private val testRule = MviTestRule<SignUpState> { sourceEvents, timeline ->
    val validator = Validator()

    SignUpModel.createSource(
        intentionsSubject,
        sourceEvents,
        SourceCreatedUseCase(UNTOUCHED),
        ValidatePhoneNumberUseCase(timeline, validator)
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

      // then
      val invalidPhoneNumberState = UNTOUCHED
          .unmetPhoneNumberConditions(PhoneNumber.values().toSet())
      testRule.assertStates(invalidPhoneNumberState)
    }
  }

  private fun typePhoneNumber(phoneNumber: String) {
    intentionsSubject.onNext(EnterPhoneNumberIntention(phoneNumber))
  }
}
