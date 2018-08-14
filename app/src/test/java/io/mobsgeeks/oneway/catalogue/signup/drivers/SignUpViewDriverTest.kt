package io.mobsgeeks.oneway.catalogue.signup.drivers

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import io.mobsgeeks.oneway.catalogue.base.TestSchedulersProvider
import io.mobsgeeks.oneway.catalogue.signup.SignUpState
import io.mobsgeeks.oneway.catalogue.signup.SignUpView
import io.mobsgeeks.oneway.catalogue.signup.drivers.SignUpViewDriver.Companion.SHOW_ERROR_DEBOUNCE_MILLIS
import io.mobsgeeks.oneway.catalogue.signup.form.PhoneNumberCondition.STARTS_WITH
import io.mobsgeeks.oneway.catalogue.signup.form.PhoneNumberCondition.values
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit.MILLISECONDS

class SignUpViewDriverTest {
  private val view = mock<SignUpView>()
  private val schedulersProvider = TestSchedulersProvider()
  private val viewDriver = SignUpViewDriver(view, schedulersProvider)
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
    verifyNoMoreInteractions(view)
  }

  @Test fun `it should not display error if username field is untouched`() {
    val untouchedUsernameState = SignUpState.UNTOUCHED
        .unmetPhoneNumberConditions(emptySet())

    // when
    feedStateToSource(untouchedUsernameState)

    // then
    verify(view).hideUsernameError()
    verifyNoMoreInteractions(view)
  }

  private fun feedStateToSource(state: SignUpState, advanceTimeByMillis: Long = SHOW_ERROR_DEBOUNCE_MILLIS) {
    sourceSubject.onNext(state)
    schedulersProvider.testScheduler.advanceTimeBy(advanceTimeByMillis, MILLISECONDS)
  }
}
