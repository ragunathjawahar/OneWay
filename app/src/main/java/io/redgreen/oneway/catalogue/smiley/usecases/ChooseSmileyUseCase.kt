package io.redgreen.oneway.catalogue.smiley.usecases

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.withLatestFrom
import io.redgreen.oneway.catalogue.smiley.ChooseSmileyIntention
import io.redgreen.oneway.catalogue.smiley.SmileyState

class ChooseSmileyUseCase(
    private val sourceCopy: Observable<SmileyState>
) : ObservableTransformer<ChooseSmileyIntention, SmileyState> {
  override fun apply(
      chooseSmileyIntentions: Observable<ChooseSmileyIntention>
  ): ObservableSource<SmileyState> {
    return chooseSmileyIntentions.withLatestFrom(sourceCopy) { intention, state ->
      state.setSmiley(intention.smiley)
    }
  }
}
