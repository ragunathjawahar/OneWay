package io.redgreen.oneway.catalogue.budapest

import io.reactivex.Observable
import io.redgreen.oneway.SourceEvent
import io.redgreen.oneway.catalogue.budapest.usecases.BudapestUseCases

/**
 * This example emits a new state for every incoming intention and does not
 * require state reduction.
 */
object BudapestModel {
  fun createSource(
      intentions: Observable<BudapestIntention>,
      sourceEvents: Observable<SourceEvent>,
      useCases: BudapestUseCases
  ): Observable<BudapestState> {
    return Observable.merge(
        sourceEvents.compose(useCases.sourceCreatedUseCase),
        sourceEvents.compose(useCases.sourceRestoredUseCase),
        intentions.ofType(EnterNameIntention::class.java).compose(useCases.enterNameUseCase)
    )
  }
}
