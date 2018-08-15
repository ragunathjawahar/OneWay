package io.redgreen.oneway.catalogue.counter

import io.reactivex.Observable
import io.redgreen.oneway.IntentionsGroup

class CounterIntentionsGroup(
    private val incrementClicks: Observable<Unit>,
    private val decrementClicks: Observable<Unit>
) : IntentionsGroup<CounterIntention> {
  override fun intentions(): Observable<CounterIntention> =
      Observable.merge(
          increment().share(),
          decrement().share()
      )

  private fun increment(): Observable<Increment> =
      incrementClicks.map { Increment }

  private fun decrement(): Observable<Decrement> =
      decrementClicks.map { Decrement }
}
