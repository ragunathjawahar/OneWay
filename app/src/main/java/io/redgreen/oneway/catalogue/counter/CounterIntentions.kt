package io.redgreen.oneway.catalogue.counter

import io.reactivex.Observable
import io.redgreen.oneway.Intentions

class CounterIntentions(
    private val incrementClicks: Observable<Unit>,
    private val decrementClicks: Observable<Unit>
) : Intentions<CounterIntention> {
  override fun stream(): Observable<CounterIntention> =
      Observable.merge(
          increment().share(),
          decrement().share()
      )

  private fun increment(): Observable<Increment> =
      incrementClicks.map { Increment }

  private fun decrement(): Observable<Decrement> =
      decrementClicks.map { Decrement }
}
