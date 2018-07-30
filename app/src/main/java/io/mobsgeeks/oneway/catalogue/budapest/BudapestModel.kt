package io.mobsgeeks.oneway.catalogue.budapest

import io.mobsgeeks.oneway.Binding
import io.mobsgeeks.oneway.catalogue.budapest.usecases.BudapestUseCases
import io.reactivex.Observable

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
