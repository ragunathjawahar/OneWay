package io.mobsgeeks.oneway.catalogue.budapest.usecases

import io.mobsgeeks.oneway.catalogue.budapest.BudapestState
import io.mobsgeeks.oneway.usecases.DefaultSourceCreatedUseCase
import io.mobsgeeks.oneway.usecases.DefaultSourceRestoredUseCase
import io.reactivex.Observable

class BudapestUseCases(
    initialState: BudapestState,
    timeline: Observable<BudapestState>
) {
  val sourceCreatedUseCase = DefaultSourceCreatedUseCase(initialState)
  val sourceRestoredUseCase = DefaultSourceRestoredUseCase(timeline)
  val enterNameUseCase = EnterNameUseCase()
}
