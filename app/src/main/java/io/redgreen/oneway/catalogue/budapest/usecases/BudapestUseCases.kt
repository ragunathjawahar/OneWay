package io.redgreen.oneway.catalogue.budapest.usecases

import io.reactivex.Observable
import io.redgreen.oneway.catalogue.budapest.BudapestState
import io.redgreen.oneway.catalogue.budapest.StrangerState
import io.redgreen.oneway.usecases.SourceCreatedUseCase
import io.redgreen.oneway.usecases.SourceRestoredUseCase

class BudapestUseCases(
    sourceCopy: Observable<BudapestState>
) {
  val sourceCreatedUseCase = SourceCreatedUseCase(StrangerState)
  val sourceRestoredUseCase = SourceRestoredUseCase(sourceCopy)
  val enterNameUseCase = EnterNameUseCase(sourceCopy)
  val noNameUseCase = NoNameUseCase(sourceCopy)
}
