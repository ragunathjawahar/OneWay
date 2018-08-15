package io.redgreen.oneway.catalogue.signup

import io.reactivex.Observable
import io.redgreen.oneway.IntentionsGroup

class SignUpIntentionsGroup(
    private val view: SignUpView,
    private val phoneNumberTextChanges: Observable<CharSequence>,
    private val phoneNumberFocusChanges: Observable<Boolean>,
    private val usernameTextChanges: Observable<CharSequence>,
    private val usernameFocusChanges: Observable<Boolean>
) : IntentionsGroup<SignUpIntention> {
  override fun intentions(): Observable<SignUpIntention> =
      Observable.merge(
          enterPhoneNumber().share(),
          loseFocusPhoneNumber().share(),
          enterUsername().share(),
          loseFocusUsername().share()
      )

  private fun enterPhoneNumber(): Observable<EnterInputIntention> =
      phoneNumberTextChanges
          .map { it.toString() }
          .map { EnterInputIntention.phoneNumber(it) }

  private fun loseFocusPhoneNumber(): Observable<LoseFocusIntention> =
      phoneNumberFocusChanges
          .filter { !it }
          .map { LoseFocusIntention.phoneNumber(view.getPhoneNumber()) }

  private fun enterUsername(): Observable<EnterInputIntention> =
      usernameTextChanges
          .map { it.toString() }
          .map { EnterInputIntention.username(it) }

  private fun loseFocusUsername(): Observable<LoseFocusIntention> =
      usernameFocusChanges
          .filter { !it }
          .map { LoseFocusIntention.username(view.getUsername()) }
}
