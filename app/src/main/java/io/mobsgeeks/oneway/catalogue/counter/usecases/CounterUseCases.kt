package io.mobsgeeks.oneway.catalogue.counter.usecases

import io.mobsgeeks.oneway.catalogue.counter.CounterState
import io.mobsgeeks.oneway.usecases.DefaultSourceCreatedUseCase
import io.mobsgeeks.oneway.usecases.DefaultSourceRestoredUseCase
import io.reactivex.Observable

class CounterUseCases(
    initialState: CounterState,
    timeline: Observable<CounterState>
) {
  val sourceCreatedUseCase = DefaultSourceCreatedUseCase(initialState)
  val sourceRestoredUseCase = DefaultSourceRestoredUseCase(timeline)
  val incrementUseCase = AddDeltaUseCase(timeline, +1)
  val decrementUseCase = AddDeltaUseCase(timeline, -1)
}
