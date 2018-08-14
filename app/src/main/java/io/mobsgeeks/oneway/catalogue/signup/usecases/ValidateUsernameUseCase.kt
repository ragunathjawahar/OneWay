package io.mobsgeeks.oneway.catalogue.signup.usecases

import io.mobsgeeks.oneway.catalogue.signup.EnterUsernameIntention
import io.mobsgeeks.oneway.catalogue.signup.SignUpState
import io.mobsgeeks.oneway.catalogue.signup.form.UsernameCondition
import io.mobsgeeks.oneway.catalogue.signup.form.Validator
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.withLatestFrom

class ValidateUsernameUseCase(
    private val timeline: Observable<SignUpState>,
    private val validator: Validator
) : ObservableTransformer<EnterUsernameIntention, SignUpState> {
  override fun apply(
      usernameIntentions: Observable<EnterUsernameIntention>
  ): ObservableSource<SignUpState> {
    return usernameIntentions
        .map { it.username }
        .map { validator.validate<UsernameCondition>(it) }
        .withLatestFrom(timeline) { unmetConditions, state ->
          state.unmetUsernameConditions(unmetConditions)
        }
  }
}
