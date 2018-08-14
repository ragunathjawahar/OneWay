package io.mobsgeeks.oneway.catalogue.signup

import io.mobsgeeks.oneway.SourceEvent
import io.mobsgeeks.oneway.catalogue.signup.usecases.ValidatePhoneNumberUseCase
import io.mobsgeeks.oneway.usecases.SourceCreatedUseCase
import io.reactivex.Observable

object SignUpModel {
  fun createSource(
      intentions: Observable<SignUpIntention>,
      sourceEvents: Observable<SourceEvent>,
      sourceCreatedUseCase: SourceCreatedUseCase<SignUpState>,
      validatePhoneNumberUseCase: ValidatePhoneNumberUseCase
  ): Observable<SignUpState> {
    return Observable.merge(
        sourceEvents.compose(sourceCreatedUseCase),
        intentions.ofType(EnterPhoneNumberIntention::class.java).compose(validatePhoneNumberUseCase)
    )
  }
}
