package io.redgreen.oneway.catalogue.signup.usecases

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.withLatestFrom
import io.redgreen.oneway.catalogue.signup.SignUpCtaIntention
import io.redgreen.oneway.catalogue.signup.SignUpState
import io.redgreen.oneway.catalogue.signup.form.PhoneNumberCondition
import io.redgreen.oneway.catalogue.signup.form.UsernameCondition
import io.redgreen.oneway.catalogue.signup.form.Validator

class SignUpCtaUseCase(
    private val timeline: Observable<SignUpState>,
    private val validator: Validator
) : ObservableTransformer<SignUpCtaIntention, SignUpState> {
  override fun apply(
      signUpCtaIntentions: Observable<SignUpCtaIntention>
  ): ObservableSource<SignUpState> {
    return signUpCtaIntentions
        .withLatestFrom(timeline) { signUpCtaIntention, state ->
          val usernameUnmetConditions = validator.validate<UsernameCondition>(signUpCtaIntention.username)
          val phoneNumberUnmetConditions = validator.validate<PhoneNumberCondition>(signUpCtaIntention.phoneNumber)
          state
              .usernameUnmetConditions(usernameUnmetConditions)
              .usernameDisplayError(usernameUnmetConditions.isNotEmpty())
              .phoneNumberUnmetConditions(phoneNumberUnmetConditions)
              .phoneNumberDisplayError(phoneNumberUnmetConditions.isNotEmpty())
        }
  }
}
