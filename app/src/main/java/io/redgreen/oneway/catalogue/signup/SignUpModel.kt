package io.redgreen.oneway.catalogue.signup

import io.reactivex.Observable
import io.redgreen.oneway.SourceEvent
import io.redgreen.oneway.catalogue.signup.usecases.SignUpUseCases

object SignUpModel {
  fun createSource(
      intentions: Observable<SignUpIntention>,
      sourceEvents: Observable<SourceEvent>,
      useCases: SignUpUseCases
  ): Observable<SignUpState> {
    return Observable.merge(
        sourceEvents.compose(useCases.sourceCreatedUseCase),
        sourceEvents.compose(useCases.sourceRestoredUseCase),
        intentions.ofType(EnterPhoneNumberIntention::class.java).compose(useCases.validatePhoneNumberUseCase),
        intentions.ofType(EnterUsernameIntention::class.java).compose(useCases.validateUsernameUseCase)
    )
  }
}
