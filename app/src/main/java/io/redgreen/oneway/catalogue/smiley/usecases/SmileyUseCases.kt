package io.redgreen.oneway.catalogue.smiley.usecases

import io.reactivex.Observable
import io.redgreen.oneway.catalogue.smiley.SmileyState
import io.redgreen.oneway.usecases.SourceCreatedUseCase
import io.redgreen.oneway.usecases.SourceRestoredUseCase

class SmileyUseCases(
    initialState: SmileyState,
    sourceCopy: Observable<SmileyState>
) {
  val sourceCreatedUseCase = SourceCreatedUseCase(initialState)
  val sourceRestoredUseCase = SourceRestoredUseCase(sourceCopy)
}
