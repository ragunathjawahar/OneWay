package io.mobsgeeks.oneway.catalogue.budapest.usecases

import io.mobsgeeks.oneway.catalogue.budapest.BudapestState
import io.mobsgeeks.oneway.usecases.DefaultSourceCreatedUseCase
import io.mobsgeeks.oneway.usecases.DefaultSourceRestoredUseCase

class BudapestUseCases(
    val createdUseCase: DefaultSourceCreatedUseCase<BudapestState>,
    val restoredUseCase: DefaultSourceRestoredUseCase<BudapestState>,
    val nameChangeUseCase: NameChangeUseCase
)
