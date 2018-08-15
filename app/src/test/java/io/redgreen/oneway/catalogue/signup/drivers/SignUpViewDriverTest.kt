package io.redgreen.oneway.catalogue.signup.drivers

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.catalogue.base.TestSchedulersProvider
import io.redgreen.oneway.catalogue.signup.SignUpState
import io.redgreen.oneway.catalogue.signup.SignUpView
import io.redgreen.oneway.catalogue.signup.drivers.SignUpViewDriver.Companion.SHOW_ERROR_DEBOUNCE_MILLIS
import io.redgreen.oneway.catalogue.signup.form.PhoneNumberCondition
import io.redgreen.oneway.catalogue.signup.form.PhoneNumberCondition.STARTS_WITH
import io.redgreen.oneway.catalogue.signup.form.PhoneNumberCondition.values
import io.redgreen.oneway.catalogue.signup.form.UsernameCondition
import io.redgreen.oneway.catalogue.signup.form.UsernameCondition.MIN_LENGTH
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit.MILLISECONDS

class SignUpViewDriverTest {
  private val view = mock<SignUpView>()
  private val schedulersProvider = TestSchedulersProvider()
  private val viewDriver = SignUpViewDriver(view, schedulersProvider)
  private val displayErrorEventsTestObserver = viewDriver.displayErrorEvents().test()
  private val sourceSubject = PublishSubject.create<SignUpState>()
  private lateinit var disposable: Disposable

  @Before fun setup() {
    disposable = viewDriver.render(sourceSubject)
  }

  @After fun teardown() {
    disposable.dispose()
  }

  @Test fun `it should not display error if error state was within display error threshold`() {
    // given
    val invalidPhoneNumberState = SignUpState.UNTOUCHED
        .unmetPhoneNumberConditions(values().toSet())
    val anotherInvalidPhoneNumberState = invalidPhoneNumberState
        .unmetPhoneNumberConditions(values().toSet().minus(STARTS_WITH))

    // when
    val thresholdMinus1Millis = SHOW_ERROR_DEBOUNCE_MILLIS - 1
    feedStateToSource(invalidPhoneNumberState, thresholdMinus1Millis)
    feedStateToSource(anotherInvalidPhoneNumberState, thresholdMinus1Millis)

    // then
    verifyZeroInteractions(view)
  }

  @Test fun `it should not display error if phone number field is untouched`() {
    // given
    val untouchedPhoneNumberState = SignUpState.UNTOUCHED
        .unmetUsernameConditions(emptySet())

    // when
    feedStateToSource(untouchedPhoneNumberState)

    // then
    verify(view).hidePhoneNumberError()

    verify(view).hideUsernameError()
    verifyNoMoreInteractions(view)
  }

  @Test fun `it should not display error if phone number field is valid`() {
    // given
    val validPhoneNumberState = SignUpState.UNTOUCHED
        .unmetPhoneNumberConditions(emptySet())

    // when
    feedStateToSource(validPhoneNumberState)

    // then
    verify(view).hidePhoneNumberError()

    verify(view).hideUsernameError()
    verifyNoMoreInteractions(view)
  }

  @Test fun `it should display error if phone number has errors and has exceeded error threshold`() {
    // given
    val invalidPhoneNumberState = SignUpState.UNTOUCHED
        .unmetPhoneNumberConditions(PhoneNumberCondition.values().toSet())

    // when
    feedStateToSource(invalidPhoneNumberState)

    // then
    verify(view).showPhoneNumberErrors(invalidPhoneNumberState.phoneNumberField.unmetConditions)

    verify(view).hideUsernameError()
    verifyNoMoreInteractions(view)
  }

  @Test fun `it should not display error if username field is untouched`() {
    val untouchedUsernameState = SignUpState.UNTOUCHED
        .unmetPhoneNumberConditions(emptySet())

    // when
    feedStateToSource(untouchedUsernameState)

    // then
    verify(view).hideUsernameError()

    verify(view).hidePhoneNumberError()
    verifyNoMoreInteractions(view)
  }

  @Test fun `it should not display error if username field is valid`() {
    // given
    val validUsernameState = SignUpState.UNTOUCHED
        .unmetUsernameConditions(emptySet())

    // when
    feedStateToSource(validUsernameState)

    // then
    verify(view).hideUsernameError()

    verify(view).hidePhoneNumberError()
    verifyNoMoreInteractions(view)
  }

  @Test fun `it should display error if username has errors and has exceeded error threshold`() {
    // given
    val invalidUsernameState = SignUpState.UNTOUCHED
        .unmetUsernameConditions(UsernameCondition.values().toSet())

    // when
    feedStateToSource(invalidUsernameState)

    // then
    verify(view).showUsernameErrors(invalidUsernameState.usernameField.unmetConditions)

    verify(view).hidePhoneNumberError()
    verifyNoMoreInteractions(view)
  }

  @Test fun `it should notify when phone number displays an error`() {
    // given
    val phoneNumberHasErrorsState = SignUpState.UNTOUCHED
        .unmetPhoneNumberConditions(setOf(STARTS_WITH))

    // when
    feedStateToSource(phoneNumberHasErrorsState)

    // then
    displayErrorEventsTestObserver.assertValues(
        DisplayPhoneNumberErrorEvent(true),
        DisplayUsernameErrorEvent(false)
    )
  }

  @Test fun `it should notify when phone number stops displaying an error`() {
    // given
    val validPhoneNumberState = SignUpState.UNTOUCHED
        .unmetPhoneNumberConditions(emptySet())

    // when
    feedStateToSource(validPhoneNumberState)

    // then
    displayErrorEventsTestObserver.assertValues(
        DisplayPhoneNumberErrorEvent(false),
        DisplayUsernameErrorEvent(false)
    )
  }

  @Test fun `it should notify when username displays an error`() {
    // given
    val usernameHasErrorsState = SignUpState.UNTOUCHED
        .unmetUsernameConditions(setOf(MIN_LENGTH))

    // when
    feedStateToSource(usernameHasErrorsState)

    // then
    displayErrorEventsTestObserver.assertValues(
        DisplayPhoneNumberErrorEvent(false),
        DisplayUsernameErrorEvent(true)
    )
  }

  @Test fun `it should notify when username stops displaying an error`() {
    // given
    val validUsernameState = SignUpState.UNTOUCHED
        .unmetUsernameConditions(emptySet())

    // when
    feedStateToSource(validUsernameState)

    // then
    displayErrorEventsTestObserver.assertValues(
        DisplayPhoneNumberErrorEvent(false),
        DisplayUsernameErrorEvent(false)
    )
  }

  private fun feedStateToSource(
      state: SignUpState,
      advanceTimeByMillis: Long = SHOW_ERROR_DEBOUNCE_MILLIS
  ) {
    sourceSubject.onNext(state)
    schedulersProvider.testScheduler.advanceTimeBy(advanceTimeByMillis, MILLISECONDS)
  }
}
