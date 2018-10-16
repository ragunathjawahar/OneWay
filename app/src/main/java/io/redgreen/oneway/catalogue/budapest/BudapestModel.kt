package io.redgreen.oneway.catalogue.budapest

import io.reactivex.Observable
import io.redgreen.oneway.SourceLifecycleEvent
import io.redgreen.oneway.catalogue.budapest.usecases.BudapestUseCases

/**
 * This example emits a new state for every incoming intention and does not
 * require state reduction.
 */
object BudapestModel {
  fun createSource(
      intentions: Observable<BudapestIntention>,
      sourceLifecycleEvents: Observable<SourceLifecycleEvent>,
      useCases: BudapestUseCases
  ): Observable<BudapestState> {
    return Observable.merge(
        sourceLifecycleEvents.compose(useCases.sourceCreatedUseCase),
        sourceLifecycleEvents.compose(useCases.sourceRestoredUseCase),
        intentions.ofType(EnterNameIntention::class.java).compose(useCases.enterNameUseCase)
    )
  }
}
