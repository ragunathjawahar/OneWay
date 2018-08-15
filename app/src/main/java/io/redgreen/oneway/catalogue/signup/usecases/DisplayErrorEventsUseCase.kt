package io.redgreen.oneway.catalogue.signup.usecases

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.withLatestFrom
import io.redgreen.oneway.catalogue.signup.SignUpState
import io.redgreen.oneway.catalogue.signup.drivers.DisplayErrorEvent
import io.redgreen.oneway.catalogue.signup.drivers.DisplayPhoneNumberErrorEvent

class DisplayErrorEventsUseCase(
    private val timeline: Observable<SignUpState>
) : ObservableTransformer<DisplayErrorEvent, SignUpState> {
  override fun apply(
      displayErrorEvents: Observable<DisplayErrorEvent>
  ): ObservableSource<SignUpState> {
    return displayErrorEvents
        .ofType(DisplayPhoneNumberErrorEvent::class.java)
        .withLatestFrom(timeline) { errorEvent, state ->
          state.displayingPhoneNumberError(errorEvent.displaying)
        }

  }
}
