package io.redgreen.oneway.catalogue.budapest.usecases

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.withLatestFrom
import io.redgreen.oneway.catalogue.budapest.BudapestState
import io.redgreen.oneway.catalogue.budapest.GreeterState
import io.redgreen.oneway.catalogue.budapest.NoNameIntention
import io.redgreen.oneway.catalogue.budapest.StrangerState

class NoNameUseCase(
    private val sourceCopy: Observable<BudapestState>
) : ObservableTransformer<NoNameIntention, BudapestState> {
  override fun apply(
      noNameIntentions: Observable<NoNameIntention>
  ): ObservableSource<BudapestState> {
    return noNameIntentions
        .withLatestFrom(sourceCopy) { _, state ->
          when (state) {
            is StrangerState -> state.noName()
            is GreeterState -> state.noName()
          }
        }
  }
}
