package io.redgreen.oneway.catalogue.smiley

import io.reactivex.Observable
import io.redgreen.oneway.SourceLifecycleEvent
import io.redgreen.oneway.catalogue.smiley.usecases.SmileyUseCases

object SmileyModel {
  fun createSource(
      sourceLifecycleEvents: Observable<SourceLifecycleEvent>,
      smileyUseCases: SmileyUseCases
  ): Observable<SmileyState> {
    return Observable.merge(
        sourceLifecycleEvents.compose(smileyUseCases.sourceCreatedUseCase),
        sourceLifecycleEvents.compose(smileyUseCases.sourceRestoredUseCase)
    )
  }
}
