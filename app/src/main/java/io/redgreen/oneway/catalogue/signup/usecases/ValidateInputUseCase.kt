package io.redgreen.oneway.catalogue.signup.usecases

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.withLatestFrom
import io.redgreen.oneway.catalogue.signup.EnterInputIntention
import io.redgreen.oneway.catalogue.signup.SignUpState
import io.redgreen.oneway.catalogue.signup.form.*
import io.redgreen.oneway.catalogue.signup.form.WhichField.PHONE_NUMBER
import io.redgreen.oneway.catalogue.signup.form.WhichField.USERNAME

class ValidateInputUseCase(
    private val timeline: Observable<SignUpState>,
    private val validator: Validator
) : ObservableTransformer<EnterInputIntention, SignUpState> {
  override fun apply(
      enterInputIntentions: Observable<EnterInputIntention>
  ): ObservableSource<SignUpState> {
    return Observable.merge(
        getPhoneNumberValidationStates(enterInputIntentions),
        getUsernameValidationStates(enterInputIntentions)
    )
  }

  private fun getPhoneNumberValidationStates(
      enterInputIntentions: Observable<EnterInputIntention>
  ): Observable<SignUpState> {
    return validateField<PhoneNumberCondition>(PHONE_NUMBER, enterInputIntentions) { state, unmetConditions ->
      state.unmetPhoneNumberConditions(unmetConditions)
    }
  }

  private fun getUsernameValidationStates(
      enterInputIntentions: Observable<EnterInputIntention>
  ): Observable<SignUpState> {
    return validateField<UsernameCondition>(USERNAME, enterInputIntentions) { state, unmetConditions ->
      state.unmetUsernameConditions(unmetConditions)
    }
  }

  private inline fun <reified T> validateField(
      field: WhichField,
      enterInputIntentions: Observable<EnterInputIntention>,
      crossinline stateReducer: (SignUpState, Set<T>) -> SignUpState
  ): Observable<SignUpState> where T : Enum<T>, T : Condition {
    return enterInputIntentions
        .filter { it.whichField == field }
        .map { validator.validate<T>(it.text) }
        .withLatestFrom(timeline) { unmetConditions, state ->
          stateReducer(state, unmetConditions)
        }
  }
}
