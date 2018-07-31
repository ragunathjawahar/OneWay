package io.mobsgeeks.oneway.catalogue.budapest

import io.mobsgeeks.oneway.Binding
import io.mobsgeeks.oneway.catalogue.budapest.usecases.BudapestUseCases
import io.reactivex.Observable

/**
 * This example emits a new state for every incoming intention and does not
 * require state reduction.
 */
object BudapestModel {
  fun bind(
      intentions: Observable<BudapestIntention>,
      bindings: Observable<Binding>,
      useCases: BudapestUseCases
  ): Observable<BudapestState> {
    return Observable.merge(
        bindings.compose(useCases.createdUseCase),
        bindings.compose(useCases.restoredUseCase),
        intentions.ofType(NameChangeIntention::class.java).compose(useCases.nameChangeUseCase)
    )
  }
}
