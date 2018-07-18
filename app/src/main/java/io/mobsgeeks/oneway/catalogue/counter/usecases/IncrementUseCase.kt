package io.mobsgeeks.oneway.catalogue.counter.usecases

import io.mobsgeeks.oneway.catalogue.counter.CounterState
import io.mobsgeeks.oneway.catalogue.counter.Increment
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.withLatestFrom

class IncrementUseCase(
    private val timeline: Observable<CounterState>
) : ObservableTransformer<Increment, CounterState> {
  override fun apply(upstream: Observable<Increment>): ObservableSource<CounterState> =
      upstream
          .withLatestFrom(timeline) { _, state -> state.add(1) }
}
