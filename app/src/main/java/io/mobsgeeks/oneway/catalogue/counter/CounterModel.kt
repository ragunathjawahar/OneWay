package io.mobsgeeks.oneway.catalogue.counter

import io.mobsgeeks.oneway.SourceEvent
import io.mobsgeeks.oneway.catalogue.counter.usecases.CounterUseCases
import io.reactivex.Observable

object CounterModel {
  fun bind(
      intentions: Observable<CounterIntention>,
      sourceEvents: Observable<SourceEvent>,
      useCases: CounterUseCases
  ): Observable<CounterState> =
      Observable.merge(
          sourceEvents.compose(useCases.createdUseCase),
          sourceEvents.compose(useCases.restoredUseCase),
          intentions.ofType(Increment::class.java).compose(useCases.incrementUseCase),
          intentions.ofType(Decrement::class.java).compose(useCases.decrementUseCase)
      )
}
