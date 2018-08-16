package io.redgreen.oneway.catalogue.signup.usecases

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.withLatestFrom
import io.redgreen.oneway.catalogue.signup.SignUpState
import io.redgreen.oneway.catalogue.signup.drivers.DisplayErrorEvent
import io.redgreen.oneway.catalogue.signup.form.WhichField.PHONE_NUMBER
import io.redgreen.oneway.catalogue.signup.form.WhichField.USERNAME

class DisplayErrorEventsUseCase(
    private val timeline: Observable<SignUpState>
) : ObservableTransformer<DisplayErrorEvent, SignUpState> {
  override fun apply(
      displayErrorEvents: Observable<DisplayErrorEvent>
  ): ObservableSource<SignUpState> {
    return displayErrorEvents
        .withLatestFrom(timeline) { errorEvent, state ->
          when (errorEvent.whichField) {
            PHONE_NUMBER -> state.phoneNumberDisplayError(errorEvent.display)
            USERNAME -> state.usernameDisplayError(errorEvent.display)
          }
        }
  }
}
