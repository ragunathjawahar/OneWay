package io.mobsgeeks.oneway.catalogue.budapest.usecases

import io.mobsgeeks.oneway.catalogue.budapest.BudapestState
import io.mobsgeeks.oneway.usecases.DefaultBindingCreatedUseCase
import io.mobsgeeks.oneway.usecases.DefaultBindingRestoredUseCase

class BudapestUseCases(
    val createdUseCase: DefaultBindingCreatedUseCase<BudapestState>,
    val restoredUseCase: DefaultBindingRestoredUseCase<BudapestState>
)
