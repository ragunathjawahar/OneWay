package io.mobsgeeks.oneway.catalogue.budapest.usecases

import io.mobsgeeks.oneway.catalogue.budapest.BudapestState
import io.mobsgeeks.oneway.usecases.SourceCreatedUseCase
import io.mobsgeeks.oneway.usecases.SourceRestoredUseCase
import io.reactivex.Observable

class BudapestUseCases(
    initialState: BudapestState,
    timeline: Observable<BudapestState>
) {
  val sourceCreatedUseCase = SourceCreatedUseCase(initialState)
  val sourceRestoredUseCase = SourceRestoredUseCase(timeline)
  val enterNameUseCase = EnterNameUseCase()
}
