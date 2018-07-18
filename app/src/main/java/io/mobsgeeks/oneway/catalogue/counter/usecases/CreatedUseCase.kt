package io.mobsgeeks.oneway.catalogue.counter.usecases

import io.mobsgeeks.oneway.Binding
import io.mobsgeeks.oneway.Binding.CREATED
import io.mobsgeeks.oneway.catalogue.counter.CounterState
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class CreatedUseCase : ObservableTransformer<Binding, CounterState> {
  override fun apply(upstream: Observable<Binding>): ObservableSource<CounterState> {
    return upstream
        .filter { it == CREATED }
        .map { CounterState.ZERO }
  }
}
