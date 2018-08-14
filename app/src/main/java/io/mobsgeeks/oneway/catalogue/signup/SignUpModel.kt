package io.mobsgeeks.oneway.catalogue.signup

import io.mobsgeeks.oneway.SourceEvent
import io.mobsgeeks.oneway.catalogue.signup.usecases.SignUpUseCases
import io.reactivex.Observable

object SignUpModel {
  fun createSource(
      intentions: Observable<SignUpIntention>,
      sourceEvents: Observable<SourceEvent>,
      useCases: SignUpUseCases
  ): Observable<SignUpState> {
    return Observable.merge(
        sourceEvents.compose(useCases.sourceCreatedUseCase),
        intentions.ofType(EnterPhoneNumberIntention::class.java).compose(useCases.validatePhoneNumberUseCase),
        intentions.ofType(EnterUsernameIntention::class.java).compose(useCases.validateUsernameUseCase)
    )
  }
}
