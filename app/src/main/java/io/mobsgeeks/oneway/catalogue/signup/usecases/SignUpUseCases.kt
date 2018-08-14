package io.mobsgeeks.oneway.catalogue.signup.usecases

import io.mobsgeeks.oneway.catalogue.signup.SignUpState
import io.mobsgeeks.oneway.catalogue.signup.form.Validator
import io.mobsgeeks.oneway.usecases.SourceCreatedUseCase
import io.reactivex.Observable

class SignUpUseCases(
    initialState: SignUpState,
    timeline: Observable<SignUpState>,
    validator: Validator
) {
  val sourceCreatedUseCase = SourceCreatedUseCase(initialState)
  val validatePhoneNumberUseCase = ValidatePhoneNumberUseCase(timeline, validator)
  val validateUsernameUseCase = ValidateUsernameUseCase(timeline, validator)
}
