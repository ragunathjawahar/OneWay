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

  private fun enterPhoneNumber(): Observable<EnterPhoneNumberIntention> =
      phoneNumberTextChanges
          .map { it.toString() }
          .map { EnterPhoneNumberIntention(it) }

  private fun enterUsername(): Observable<EnterUsernameIntention> =
      usernameTextChanges
          .map { it.toString() }
          .map { EnterUsernameIntention(it) }
}
