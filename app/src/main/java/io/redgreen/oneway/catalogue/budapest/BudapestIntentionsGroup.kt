package io.redgreen.oneway.catalogue.budapest

import io.reactivex.Observable
import io.redgreen.oneway.IntentionsGroup

class BudapestIntentionsGroup(
    private val nameTextChanges: Observable<CharSequence>
) : IntentionsGroup<BudapestIntention> {
  override fun intentions(): Observable<BudapestIntention> =
    enterName().share().cast(BudapestIntention::class.java)

  private fun enterName(): Observable<EnterNameIntention> =
      nameTextChanges
          .map { it.toString() }
          .map { EnterNameIntention(it) }
}
