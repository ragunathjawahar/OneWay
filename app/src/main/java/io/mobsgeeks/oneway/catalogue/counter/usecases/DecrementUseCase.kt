package io.mobsgeeks.oneway.catalogue.counter.usecases

import io.mobsgeeks.oneway.catalogue.counter.CounterState
import io.mobsgeeks.oneway.catalogue.counter.Decrement
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.withLatestFrom

class DecrementUseCase(
    private val timeline: Observable<CounterState>
) : ObservableTransformer<Decrement, CounterState> {
  override fun apply(upstream: Observable<Decrement>): ObservableSource<CounterState> =
      upstream
          .withLatestFrom(timeline) { _, state -> state.add(-1) }
}
