package io.mobsgeeks.oneway.catalogue.signup.drivers

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import io.mobsgeeks.oneway.catalogue.base.TestSchedulersProvider
import io.mobsgeeks.oneway.catalogue.signup.SignUpState
import io.mobsgeeks.oneway.catalogue.signup.SignUpView
import io.mobsgeeks.oneway.catalogue.signup.drivers.SignUpViewDriver.Companion.SHOW_ERROR_THRESHOLD_MILLIS
import io.mobsgeeks.oneway.catalogue.signup.form.PhoneNumberCondition.STARTS_WITH
import io.mobsgeeks.oneway.catalogue.signup.form.PhoneNumberCondition.values
import io.reactivex.subjects.PublishSubject
import org.junit.Test
import java.util.concurrent.TimeUnit.MILLISECONDS

class SignUpViewDriverTest {
  @Test fun `it should not display error if error state was within display error threshold`() {
    // given
    val view = mock<SignUpView>()
    val schedulersProvider = TestSchedulersProvider()
    val viewDriver = SignUpViewDriver(view, schedulersProvider)
    val sourceSubject = PublishSubject.create<SignUpState>()
    val disposable = viewDriver.render(sourceSubject)

    val invalidPhoneNumberState = SignUpState.UNTOUCHED
        .unmetPhoneNumberConditions(values().toSet())
    val anotherInvalidPhoneNumberState = invalidPhoneNumberState
        .unmetPhoneNumberConditions(values().toSet().minus(STARTS_WITH))

    // when
    val thresholdMinus1Millis = SHOW_ERROR_THRESHOLD_MILLIS - 1
    sourceSubject.onNext(invalidPhoneNumberState)
    schedulersProvider.testScheduler.advanceTimeBy(thresholdMinus1Millis, MILLISECONDS)
    sourceSubject.onNext(anotherInvalidPhoneNumberState)
    schedulersProvider.testScheduler.advanceTimeBy(thresholdMinus1Millis, MILLISECONDS)

    // then
    verifyZeroInteractions(view)
    disposable.dispose()
  }
}
