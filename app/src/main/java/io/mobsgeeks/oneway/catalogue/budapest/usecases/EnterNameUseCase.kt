package io.mobsgeeks.oneway.catalogue.budapest.usecases

import io.mobsgeeks.oneway.catalogue.budapest.BudapestState
import io.mobsgeeks.oneway.catalogue.budapest.EnterNameIntention
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class EnterNameUseCase : ObservableTransformer<EnterNameIntention, BudapestState> {
  override fun apply(enterNameIntentions: Observable<EnterNameIntention>): ObservableSource<BudapestState> =
      enterNameIntentions.map { it.name }.map { BudapestState(it) }
}
