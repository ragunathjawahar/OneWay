package io.redgreen.oneway.catalogue.smiley

import io.reactivex.Observable
import io.redgreen.oneway.SourceLifecycleEvent
import io.redgreen.oneway.catalogue.smiley.usecases.SmileyUseCases

object SmileyModel {
  fun createSource(
      intentions: Observable<SmileyIntention>,
      sourceLifecycleEvents: Observable<SourceLifecycleEvent>,
      smileyUseCases: SmileyUseCases
  ): Observable<SmileyState> {
    return Observable.merge(
        sourceLifecycleEvents.compose(smileyUseCases.sourceCreatedUseCase),
        sourceLifecycleEvents.compose(smileyUseCases.sourceRestoredUseCase),
        intentions.ofType(ChooseSmileyIntention::class.java).compose(smileyUseCases.chooseSmileyUseCase)
    )
  }
}
