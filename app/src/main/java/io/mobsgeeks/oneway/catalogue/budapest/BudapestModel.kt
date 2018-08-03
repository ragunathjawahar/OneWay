package io.mobsgeeks.oneway.catalogue.budapest

import io.mobsgeeks.oneway.SourceEvent
import io.mobsgeeks.oneway.catalogue.budapest.usecases.BudapestUseCases
import io.reactivex.Observable

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
        sourceEvents.compose(useCases.createdUseCase),
        sourceEvents.compose(useCases.restoredUseCase),
        intentions.ofType(EnterNameIntention::class.java).compose(useCases.enterNameUseCase)
    )
  }
}
