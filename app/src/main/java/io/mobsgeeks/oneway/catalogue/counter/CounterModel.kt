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

    val decrementIntentions = intentions
        .ofType(Decrement::class.java)

    return Observable.merge(
        sourceEvents.compose(useCases.sourceCreatedUseCase),
        sourceEvents.compose(useCases.sourceRestoredUseCase),
        incrementIntentions.map { Unit }.compose(useCases.incrementUseCase),
        decrementIntentions.map { Unit }.compose(useCases.decrementUseCase)
    )
  }
}
