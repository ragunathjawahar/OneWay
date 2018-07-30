package io.mobsgeeks.oneway.catalogue.budapest

import io.mobsgeeks.oneway.Binding
import io.mobsgeeks.oneway.catalogue.budapest.usecases.BudapestUseCases
import io.reactivex.Observable

object BudapestModel {
  fun bind(bindings: Observable<Binding>, useCases: BudapestUseCases): Observable<BudapestState> {
    return bindings.compose(useCases.createdUseCase)
  }
}
