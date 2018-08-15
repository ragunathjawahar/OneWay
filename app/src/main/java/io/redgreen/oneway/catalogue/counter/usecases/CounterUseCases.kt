package io.redgreen.oneway.catalogue.counter.usecases

import io.reactivex.Observable
import io.redgreen.oneway.catalogue.counter.CounterState
import io.redgreen.oneway.usecases.SourceCreatedUseCase
import io.redgreen.oneway.usecases.SourceRestoredUseCase

class CounterUseCases(
    initialState: CounterState,
    timeline: Observable<CounterState>
) {
  val sourceCreatedUseCase = SourceCreatedUseCase(initialState)
  val sourceRestoredUseCase = SourceRestoredUseCase(timeline)
  val incrementUseCase = AddDeltaUseCase(timeline, +1)
  val decrementUseCase = AddDeltaUseCase(timeline, -1)
}
