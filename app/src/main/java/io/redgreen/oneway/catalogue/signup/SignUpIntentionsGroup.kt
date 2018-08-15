package io.redgreen.oneway.catalogue.signup

import io.reactivex.Observable
import io.redgreen.oneway.IntentionsGroup

class SignUpIntentionsGroup(
    private val phoneNumberTextChanges: Observable<CharSequence>,
    private val usernameTextChanges: Observable<CharSequence>
) : IntentionsGroup<SignUpIntention> {
  override fun intentions(): Observable<SignUpIntention> =
      Observable.merge(
          enterPhoneNumber().share(),
          enterUsername().share()
      )

  private fun enterPhoneNumber(): Observable<EnterInputIntention> =
      phoneNumberTextChanges
          .map { it.toString() }
          .map { EnterInputIntention.phoneNumber(it) }

  private fun enterUsername(): Observable<EnterInputIntention> =
      usernameTextChanges
          .map { it.toString() }
          .map { EnterInputIntention.username(it) }
}
