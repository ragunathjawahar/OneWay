package io.redgreen.oneway.catalogue.budapest

import io.reactivex.Observable
import io.redgreen.oneway.Intentions

class BudapestIntentions(
    nameTextChanges: Observable<CharSequence>
) : Intentions<BudapestIntention> {
  private val sharedNameTextChanges = nameTextChanges
      .map { it.toString() }
      .share()

  override fun stream(): Observable<BudapestIntention> =
      Observable.merge(
          enterName().share(),
          noName().share()
      )

  private fun enterName(): Observable<EnterNameIntention> =
      sharedNameTextChanges
          .filter { it.isNotBlank() }
          .map { EnterNameIntention(it) }

  private fun noName(): Observable<NoNameIntention> =
      sharedNameTextChanges
          .filter { it.isBlank() }
          .map { NoNameIntention }
}
