package io.redgreen.oneway.catalogue.signup.usecases

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.withLatestFrom
import io.redgreen.oneway.catalogue.signup.EnterInputIntention
import io.redgreen.oneway.catalogue.signup.SignUpState
import io.redgreen.oneway.catalogue.signup.form.PhoneNumberCondition
import io.redgreen.oneway.catalogue.signup.form.Validator

class ValidatePhoneNumberUseCase(
    private val timeline: Observable<SignUpState>,
    private val validator: Validator
) : ObservableTransformer<EnterInputIntention, SignUpState> {
  override fun apply(
      phoneNumberIntentions: Observable<EnterInputIntention>
  ): ObservableSource<SignUpState> {
    return phoneNumberIntentions
        .map { it.text }
        .map { validator.validate<PhoneNumberCondition>(it) }
        .withLatestFrom(timeline) { unmetConditions, state ->
          state.unmetPhoneNumberConditions(unmetConditions)
        }
  }
}
