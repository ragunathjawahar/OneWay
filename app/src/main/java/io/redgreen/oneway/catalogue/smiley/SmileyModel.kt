package io.redgreen.oneway.catalogue.smiley

import io.reactivex.Observable
import io.redgreen.oneway.SourceLifecycleEvent
import io.redgreen.oneway.usecases.SourceCreatedUseCase

object SmileyModel {
  fun createSource(
      sourceLifecycleEvents: Observable<SourceLifecycleEvent>,
      sourceCreatedUseCase: SourceCreatedUseCase<SmileyState>
  ): Observable<SmileyState> {
    return sourceLifecycleEvents.compose(sourceCreatedUseCase)
  }
}
