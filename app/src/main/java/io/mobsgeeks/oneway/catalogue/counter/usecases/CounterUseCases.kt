package io.mobsgeeks.oneway.catalogue.counter.usecases

import io.mobsgeeks.oneway.catalogue.counter.CounterState
import io.mobsgeeks.oneway.usecases.DefaultSourceCreatedUseCase
import io.mobsgeeks.oneway.usecases.DefaultSourceRestoredUseCase

class CounterUseCases(
    val createdUseCase: DefaultSourceCreatedUseCase<CounterState>,
    val restoredUseCase: DefaultSourceRestoredUseCase<CounterState>,
    val incrementUseCase: IncrementUseCase,
    val decrementUseCase: DecrementUseCase
)
