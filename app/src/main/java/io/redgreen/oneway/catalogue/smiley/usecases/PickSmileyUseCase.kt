package io.redgreen.oneway.catalogue.smiley.usecases

import arrow.core.None
import arrow.core.Some
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.withLatestFrom
import io.redgreen.oneway.catalogue.smiley.PickSmileyIntention
import io.redgreen.oneway.catalogue.smiley.SmileyState
import io.redgreen.oneway.catalogue.smiley.drivers.SmileyTransientViewDriver

class PickSmileyUseCase(
    private val sourceCopy: Observable<SmileyState>,
    private val transientViewDriver: SmileyTransientViewDriver
) : ObservableTransformer<PickSmileyIntention, SmileyState> {
  override fun apply(
      pickSmileyIntentions: Observable<PickSmileyIntention>
  ): ObservableSource<SmileyState> {
    return pickSmileyIntentions
        .doOnNext { notifyIfPickSmileyCancelled(it) }
        .withLatestFrom(sourceCopy) { intention, state ->
          reduceToStateObservable(state, intention)
        }
        .flatMap { it }
  }

  private fun notifyIfPickSmileyCancelled(
      intention: PickSmileyIntention
  ) {
    if (intention.characterOption is None) {
      transientViewDriver.pickSmileyCancelled()
    }
  }

  private fun reduceToStateObservable(
      state: SmileyState,
      intention: PickSmileyIntention
  ): Observable<SmileyState> {
    val characterOption = intention.characterOption
    return when (characterOption) {
      is Some -> Observable.just(state.updateSmiley(characterOption.t))
      is None -> Observable.empty()
    }
  }
}
