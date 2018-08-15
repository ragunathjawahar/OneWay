package io.redgreen.oneway.catalogue.signup.usecases

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.withLatestFrom
import io.redgreen.oneway.catalogue.signup.EnterInputIntention
import io.redgreen.oneway.catalogue.signup.SignUpState
import io.redgreen.oneway.catalogue.signup.form.UsernameCondition
import io.redgreen.oneway.catalogue.signup.form.Validator

class ValidateUsernameUseCase(
    private val timeline: Observable<SignUpState>,
    private val validator: Validator
) : ObservableTransformer<EnterInputIntention, SignUpState> {
  override fun apply(
      usernameIntentions: Observable<EnterInputIntention>
  ): ObservableSource<SignUpState> {
    return usernameIntentions
        .map { it.text }
        .map { validator.validate<UsernameCondition>(it) }
        .withLatestFrom(timeline) { unmetConditions, state ->
          state.unmetUsernameConditions(unmetConditions)
        }
  }
}
