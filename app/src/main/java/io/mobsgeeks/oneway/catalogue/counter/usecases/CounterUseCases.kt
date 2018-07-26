package io.mobsgeeks.oneway.catalogue.counter.usecases

import io.mobsgeeks.oneway.catalogue.counter.CounterState
import io.mobsgeeks.oneway.usecases.DefaultBindingCreatedUseCase
import io.mobsgeeks.oneway.usecases.DefaultBindingRestoredUseCase

class CounterUseCases(
    val createdUseCase: DefaultBindingCreatedUseCase<CounterState>,
    val restoredUseCase: DefaultBindingRestoredUseCase<CounterState>,
    val incrementUseCase: IncrementUseCase,
    val decrementUseCase: DecrementUseCase
)
