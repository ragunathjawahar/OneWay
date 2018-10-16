package io.redgreen.oneway.catalogue.budapest

import io.reactivex.Observable
import io.redgreen.oneway.Intentions

class BudapestIntentions(
    private val nameTextChanges: Observable<CharSequence>
) : Intentions<BudapestIntention> {
  override fun stream(): Observable<BudapestIntention> =
    enterName().share().cast(BudapestIntention::class.java)

  private fun enterName(): Observable<EnterNameIntention> =
      nameTextChanges
          .map { it.toString() }
          .map { EnterNameIntention(it) }
}
