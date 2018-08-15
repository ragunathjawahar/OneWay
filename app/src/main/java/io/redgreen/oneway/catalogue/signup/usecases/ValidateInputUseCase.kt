package io.redgreen.oneway.catalogue.signup.usecases

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.withLatestFrom
import io.redgreen.oneway.catalogue.signup.EnterInputIntention
import io.redgreen.oneway.catalogue.signup.LoseFocusIntention
import io.redgreen.oneway.catalogue.signup.SignUpIntention
import io.redgreen.oneway.catalogue.signup.SignUpState
import io.redgreen.oneway.catalogue.signup.form.*
import io.redgreen.oneway.catalogue.signup.form.WhichField.PHONE_NUMBER
import io.redgreen.oneway.catalogue.signup.form.WhichField.USERNAME

class ValidateInputUseCase(
    private val timeline: Observable<SignUpState>,
    private val validator: Validator
) : ObservableTransformer<SignUpIntention, SignUpState> {
  override fun apply(
      signUpIntentions: Observable<SignUpIntention>
  ): ObservableSource<SignUpState> {
    val enterInputIntentions = signUpIntentions
        .ofType(EnterInputIntention::class.java)
        .map { it.whichField to it.text }

    val loseFocusIntentions = signUpIntentions
        .ofType(LoseFocusIntention::class.java)
        .map { it.whichField to it.text }

    return Observable.merge(
        getPhoneNumberValidationStates(enterInputIntentions) { state, unmetConditions ->
          state.unmetPhoneNumberConditions(unmetConditions)
        },

        getUsernameValidationStates(enterInputIntentions) { state, unmetConditions ->
          state.unmetUsernameConditions(unmetConditions)
        },

        getPhoneNumberValidationStates(loseFocusIntentions) { state, unmetConditions ->
          state.displayPhoneNumberErrorImmediate(unmetConditions)
        },

        getUsernameValidationStates(loseFocusIntentions) { state, unmetConditions ->
          state.displayUsernameErrorImmediate(unmetConditions)
        }
    )
  }

  private fun getPhoneNumberValidationStates(
      enterInputIntentions: Observable<Pair<WhichField, String>>,
      stateReducer: (SignUpState, Set<PhoneNumberCondition>) -> SignUpState
  ): Observable<SignUpState> {
    return validateField(PHONE_NUMBER, enterInputIntentions, stateReducer)
  }

  private fun getUsernameValidationStates(
      enterInputIntentions: Observable<Pair<WhichField, String>>,
      stateReducer: (SignUpState, Set<UsernameCondition>) -> SignUpState
  ): Observable<SignUpState> {
    return validateField(USERNAME, enterInputIntentions, stateReducer)
  }

  private inline fun <reified T> validateField(
      field: WhichField,
      enterInputIntentions: Observable<Pair<WhichField, String>>,
      crossinline stateReducer: (SignUpState, Set<T>) -> SignUpState
  ): Observable<SignUpState> where T : Enum<T>, T : Condition {
    return enterInputIntentions
        .filter { it.first == field }
        .map { validator.validate<T>(it.second) }
        .withLatestFrom(timeline) { unmetConditions, state ->
          stateReducer(state, unmetConditions)
        }
  }
}
