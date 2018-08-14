package io.mobsgeeks.oneway.catalogue.signup.usecases

import io.mobsgeeks.oneway.catalogue.signup.EnterPhoneNumberIntention
import io.mobsgeeks.oneway.catalogue.signup.SignUpState
import io.mobsgeeks.oneway.catalogue.signup.form.PhoneNumberCondition
import io.mobsgeeks.oneway.catalogue.signup.form.Validator
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.withLatestFrom

class ValidatePhoneNumberUseCase(
    private val timeline: Observable<SignUpState>,
    private val validator: Validator
) : ObservableTransformer<EnterPhoneNumberIntention, SignUpState> {
  override fun apply(
      phoneNumberIntentions: Observable<EnterPhoneNumberIntention>
  ): ObservableSource<SignUpState> {
    return phoneNumberIntentions
        .map { it.phoneNumber }
        .map { validator.validate<PhoneNumberCondition>(it) }
        .withLatestFrom(timeline) { unmetConditions, state ->
          state.unmetPhoneNumberConditions(unmetConditions)
        }
  }
}
