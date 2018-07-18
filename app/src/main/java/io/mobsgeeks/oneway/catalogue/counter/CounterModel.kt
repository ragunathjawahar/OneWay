package io.mobsgeeks.oneway.catalogue.counter

import io.mobsgeeks.oneway.Binding
import io.mobsgeeks.oneway.catalogue.counter.usecases.CounterUseCases
import io.reactivex.Observable

object CounterModel {
  fun bind(
      intentions: Observable<CounterIntention>,
      bindings: Observable<Binding>,
      useCases: CounterUseCases
  ): Observable<CounterState> =
      Observable.merge(
          bindings.compose(useCases.createdUseCase),
          bindings.compose(useCases.restoredUseCase),
          intentions.ofType(Increment::class.java).compose(useCases.incrementUseCase),
          intentions.ofType(Decrement::class.java).compose(useCases.decrementUseCase)
      )
}
