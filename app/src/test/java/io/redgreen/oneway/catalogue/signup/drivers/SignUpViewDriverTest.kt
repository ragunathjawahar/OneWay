package io.redgreen.oneway.catalogue.signup.drivers

import com.nhaarman.mockito_kotlin.*
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.catalogue.base.TestSchedulersProvider
import io.redgreen.oneway.catalogue.signup.SignUpState
import io.redgreen.oneway.catalogue.signup.SignUpView
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
  companion object {
    private const val ERROR_THRESHOLD_MILLIS = 200L
  }

  private val view = mock<SignUpView>()
  private val schedulersProvider = TestSchedulersProvider()
  private val viewDriver = SignUpViewDriver(view, schedulersProvider, ERROR_THRESHOLD_MILLIS)
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
    val thresholdMinus1Millis = ERROR_THRESHOLD_MILLIS - 1
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
    verifyNoMoreInteractions(view)
  }

  @Test fun `it should not display error if username field is untouched`() {
    val untouchedUsernameState = SignUpState.UNTOUCHED
        .unmetPhoneNumberConditions(emptySet())

    // when
    feedStateToSource(untouchedUsernameState)

    // then
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
        DisplayPhoneNumberErrorEvent(true)
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
        DisplayPhoneNumberErrorEvent(false)
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
        DisplayUsernameErrorEvent(false)
    )
  }

  @Test fun `it should display phone number error immediately if it is already showing an error`() {
    // given
    val unmetConditions1 = PhoneNumberCondition.values().toSet()
    val notDisplayingPhoneNumberErrorState = SignUpState.UNTOUCHED
        .unmetPhoneNumberConditions(unmetConditions1)

    val unmetConditions2 = setOf(STARTS_WITH)
    val displayingPhoneNumberErrorState = notDisplayingPhoneNumberErrorState
        .displayingPhoneNumberError(true)
        .unmetPhoneNumberConditions(unmetConditions2)

    val validPhoneNumberState = displayingPhoneNumberErrorState
        .unmetPhoneNumberConditions(emptySet())

    // when
    feedStateToSource(notDisplayingPhoneNumberErrorState)
    feedStateToSource(displayingPhoneNumberErrorState, 0L)
    feedStateToSource(validPhoneNumberState, 0L)

    // then
    val inOrder = inOrder(view)
    inOrder.verify(view).showPhoneNumberErrors(unmetConditions1)
    inOrder.verify(view).showPhoneNumberErrors(unmetConditions2)
    inOrder.verify(view).hidePhoneNumberError()

    verifyNoMoreInteractions(view)
  }

  @Test fun `it should display username error immediately if it is already showing an error`() {
    // given
    val unmetConditions1 = UsernameCondition.values().toSet()
    val notDisplayingUsernameErrorState = SignUpState.UNTOUCHED
        .unmetUsernameConditions(unmetConditions1)

    val unmetConditions2 = setOf(MIN_LENGTH)
    val displayingUsernameErrorState = notDisplayingUsernameErrorState
        .displayingUsernameError(true)
        .unmetUsernameConditions(unmetConditions2)

    val validUsernameState = displayingUsernameErrorState
        .unmetUsernameConditions(emptySet())

    // when
    feedStateToSource(notDisplayingUsernameErrorState)
    feedStateToSource(displayingUsernameErrorState, 0L)
    feedStateToSource(validUsernameState, 0L)

    // then
    val inOrder = inOrder(view)
    inOrder.verify(view).showUsernameErrors(unmetConditions1)
    inOrder.verify(view).showUsernameErrors(unmetConditions2)
    inOrder.verify(view).hideUsernameError()

    verifyNoMoreInteractions(view)
  }

  private fun feedStateToSource(
      state: SignUpState,
      advanceTimeByMillis: Long = ERROR_THRESHOLD_MILLIS
  ) {
    sourceSubject.onNext(state)
    schedulersProvider.testScheduler.advanceTimeBy(advanceTimeByMillis, MILLISECONDS)
  }
}
