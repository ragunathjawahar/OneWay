package io.mobsgeeks.oneway.catalogue.counter

import io.reactivex.Observable

class CounterIntentions(
    private val incrementClicks: Observable<Unit>,
    private val decrementClicks: Observable<Unit>
) {
  fun stream(): Observable<CounterIntention> =
      Observable.merge(
          increment().share(),
          decrement().share()
      )

  private fun increment(): Observable<Increment> =
      incrementClicks.map { Increment }

  private fun decrement(): Observable<Decrement> =
      decrementClicks.map { Decrement }
}
