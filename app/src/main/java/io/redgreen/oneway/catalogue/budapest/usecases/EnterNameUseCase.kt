package io.redgreen.oneway.catalogue.budapest.usecases

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.redgreen.oneway.catalogue.budapest.BudapestState
import io.redgreen.oneway.catalogue.budapest.EnterNameIntention

class EnterNameUseCase : ObservableTransformer<EnterNameIntention, BudapestState> {
  override fun apply(enterNameIntentions: Observable<EnterNameIntention>): ObservableSource<BudapestState> =
      enterNameIntentions.map { it.name }.map { BudapestState(it) }
}
