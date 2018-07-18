package io.mobsgeeks.oneway.catalogue.counter.usecases

import io.mobsgeeks.oneway.Binding
import io.mobsgeeks.oneway.Binding.RESTORED
import io.mobsgeeks.oneway.catalogue.counter.CounterState
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.withLatestFrom

class RestoredUseCase(
    private val timeline: Observable<CounterState>
) : ObservableTransformer<Binding, CounterState> {
  override fun apply(upstream: Observable<Binding>): ObservableSource<CounterState> =
      upstream
          .filter { it == RESTORED }
          .withLatestFrom(timeline) { _, state -> state }
}
