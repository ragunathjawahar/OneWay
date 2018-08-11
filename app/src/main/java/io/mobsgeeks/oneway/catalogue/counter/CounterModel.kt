package io.mobsgeeks.oneway.catalogue.counter

import io.mobsgeeks.oneway.SourceEvent
import io.mobsgeeks.oneway.catalogue.counter.usecases.CounterUseCases
import io.reactivex.Observable

object CounterModel {
  fun createSource(
      intentions: Observable<CounterIntention>,
      sourceEvents: Observable<SourceEvent>,
      useCases: CounterUseCases
  ): Observable<CounterState> {
    val incrementIntentions = intentions
        .ofType(Increment::class.java)
        .map { Unit }

    val decrementIntentions = intentions
        .ofType(Decrement::class.java)
        .map { Unit }

    return Observable.merge(
        sourceEvents.compose(useCases.sourceCreatedUseCase),
        sourceEvents.compose(useCases.sourceRestoredUseCase),
        incrementIntentions.compose(useCases.incrementUseCase),
        decrementIntentions.compose(useCases.decrementUseCase)
    )
  }
}
