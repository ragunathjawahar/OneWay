package io.redgreen.oneway.catalogue.signup

import io.reactivex.Observable
import io.redgreen.oneway.SourceLifecycleEvent
import io.redgreen.oneway.catalogue.signup.drivers.DisplayErrorEvent
import io.redgreen.oneway.catalogue.signup.usecases.SignUpUseCases

object SignUpModel {
  fun createSource(
      intentions: Observable<SignUpIntention>,
      displayErrorEvents: Observable<DisplayErrorEvent>,
      sourceLifecycleEvents: Observable<SourceLifecycleEvent>,
      useCases: SignUpUseCases
  ): Observable<SignUpState> {
    return Observable.mergeArray(
        sourceLifecycleEvents.compose(useCases.sourceCreatedUseCase),
        sourceLifecycleEvents.compose(useCases.sourceRestoredUseCase),
        intentions.compose(useCases.validateInputUseCase),
        displayErrorEvents.compose(useCases.displayErrorEventsUseCase),
        intentions.ofType(SignUpCtaIntention::class.java).compose(useCases.signUpCtaUseCase)
    )
  }
}
