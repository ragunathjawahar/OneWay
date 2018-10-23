package io.redgreen.oneway.catalogue.smiley.usecases

import arrow.core.None
import arrow.core.Some
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.withLatestFrom
import io.redgreen.oneway.catalogue.smiley.PickSmileyIntention
import io.redgreen.oneway.catalogue.smiley.SmileyState

class PickSmileyUseCase(
    private val sourceCopy: Observable<SmileyState>
) : ObservableTransformer<PickSmileyIntention, SmileyState> {
  override fun apply(
      pickSmileyIntentions: Observable<PickSmileyIntention>
  ): ObservableSource<SmileyState> {
    return pickSmileyIntentions.withLatestFrom(sourceCopy) { intention, state ->
      val smiley = intention.smiley
      when (smiley) {
        is Some -> state.updateSmiley(smiley.t)
        is None -> TODO("Yet to handle.")
      }
    }
  }
}
