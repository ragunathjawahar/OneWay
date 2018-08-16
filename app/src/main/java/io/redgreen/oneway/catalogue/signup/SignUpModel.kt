package io.redgreen.oneway.catalogue.signup

import io.reactivex.Observable
import io.redgreen.oneway.SourceEvent
import io.redgreen.oneway.catalogue.signup.drivers.DisplayErrorEvent
import io.redgreen.oneway.catalogue.signup.usecases.SignUpUseCases

object SignUpModel {
  fun createSource(
      intentions: Observable<SignUpIntention>,
      displayErrorEvents: Observable<DisplayErrorEvent>,
      sourceEvents: Observable<SourceEvent>,
      useCases: SignUpUseCases
  ): Observable<SignUpState> {
    return Observable.mergeArray(
        sourceEvents.compose(useCases.sourceCreatedUseCase),
        sourceEvents.compose(useCases.sourceRestoredUseCase),
        intentions.compose(useCases.validateInputUseCase),
        displayErrorEvents.compose(useCases.displayErrorEventsUseCase),
        intentions.ofType(SignUpCtaIntention::class.java).compose(useCases.signUpCtaUseCase)
    )
  }
}
