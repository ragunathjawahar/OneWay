package io.mobsgeeks.oneway.catalogue.budapest

import io.mobsgeeks.oneway.IntentionsGroup
import io.reactivex.Observable

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
