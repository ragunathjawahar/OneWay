package io.redgreen.oneway.catalogue.signup

import io.reactivex.Observable
import io.redgreen.oneway.SourceEvent
import io.redgreen.oneway.catalogue.signup.drivers.DisplayErrorEvent
import io.redgreen.oneway.catalogue.signup.form.WhichField.PHONE_NUMBER
import io.redgreen.oneway.catalogue.signup.form.WhichField.USERNAME
import io.redgreen.oneway.catalogue.signup.usecases.SignUpUseCases

object SignUpModel {
  fun createSource(
      intentions: Observable<SignUpIntention>,
      displayErrorEvents: Observable<DisplayErrorEvent>,
      sourceEvents: Observable<SourceEvent>,
      useCases: SignUpUseCases
  ): Observable<SignUpState> {
    val enterPhoneNumberIntentions = intentions
        .ofType(EnterInputIntention::class.java)
        .filter { it.whichField == PHONE_NUMBER }

    val enterUsernameIntentions = intentions
        .ofType(EnterInputIntention::class.java)
        .filter { it.whichField == USERNAME }

    return Observable.mergeArray(
        sourceEvents.compose(useCases.sourceCreatedUseCase),
        sourceEvents.compose(useCases.sourceRestoredUseCase),
        enterPhoneNumberIntentions.compose(useCases.validatePhoneNumberUseCase),
        enterUsernameIntentions.compose(useCases.validateUsernameUseCase),
        displayErrorEvents.compose(useCases.displayErrorEventsUseCase)
    )
  }
}
