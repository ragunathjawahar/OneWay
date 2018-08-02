package io.mobsgeeks.oneway.catalogue.counter

import io.mobsgeeks.oneway.IntentionsGroup
import io.reactivex.Observable

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
