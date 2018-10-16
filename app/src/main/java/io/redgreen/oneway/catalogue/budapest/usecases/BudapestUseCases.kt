package io.redgreen.oneway.catalogue.budapest.usecases

import io.reactivex.Observable
import io.redgreen.oneway.catalogue.budapest.BudapestState
import io.redgreen.oneway.catalogue.budapest.BudapestState.Companion.STRANGER
import io.redgreen.oneway.usecases.SourceCreatedUseCase
import io.redgreen.oneway.usecases.SourceRestoredUseCase

class BudapestUseCases(
    sourceCopy: Observable<BudapestState>
) {
  val sourceCreatedUseCase = SourceCreatedUseCase(STRANGER)
  val sourceRestoredUseCase = SourceRestoredUseCase(sourceCopy)
  val enterNameUseCase = EnterNameUseCase()
}
