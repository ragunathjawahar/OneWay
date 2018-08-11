package io.mobsgeeks.oneway.catalogue.counter.usecases

import io.mobsgeeks.oneway.catalogue.counter.CounterState
import io.mobsgeeks.oneway.usecases.SourceCreatedUseCase
import io.mobsgeeks.oneway.usecases.SourceRestoredUseCase
import io.reactivex.Observable

class CounterUseCases(
    initialState: CounterState,
    timeline: Observable<CounterState>
) {
  val sourceCreatedUseCase = SourceCreatedUseCase(initialState)
  val sourceRestoredUseCase = SourceRestoredUseCase(timeline)
  val incrementUseCase = AddDeltaUseCase(timeline, +1)
  val decrementUseCase = AddDeltaUseCase(timeline, -1)
}
