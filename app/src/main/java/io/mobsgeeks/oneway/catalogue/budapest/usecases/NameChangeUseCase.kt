package io.mobsgeeks.oneway.catalogue.budapest.usecases

import io.mobsgeeks.oneway.catalogue.budapest.BudapestState
import io.mobsgeeks.oneway.catalogue.budapest.NameChangeIntention
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class NameChangeUseCase : ObservableTransformer<NameChangeIntention, BudapestState> {
  override fun apply(nameChangeIntentions: Observable<NameChangeIntention>): ObservableSource<BudapestState> =
      nameChangeIntentions.map { it.name }.map { BudapestState(it) }
}
