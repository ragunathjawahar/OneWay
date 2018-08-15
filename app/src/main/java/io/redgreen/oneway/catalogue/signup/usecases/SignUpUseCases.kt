package io.redgreen.oneway.catalogue.signup.usecases

import io.reactivex.Observable
import io.redgreen.oneway.catalogue.signup.SignUpState
import io.redgreen.oneway.catalogue.signup.drivers.DisplayErrorEvent
import io.redgreen.oneway.catalogue.signup.form.Validator
import io.redgreen.oneway.usecases.SourceCreatedUseCase
import io.redgreen.oneway.usecases.SourceRestoredUseCase

class SignUpUseCases(
    initialState: SignUpState,
    timeline: Observable<SignUpState>,
    validator: Validator
) {
  val sourceCreatedUseCase = SourceCreatedUseCase(initialState)
  val sourceRestoredUseCase = SourceRestoredUseCase(timeline)
  val validatePhoneNumberUseCase = ValidatePhoneNumberUseCase(timeline, validator)
  val validateUsernameUseCase = ValidateUsernameUseCase(timeline, validator)
  val displayErrorEventsUseCase = DisplayErrorEventsUseCase(timeline)
}
