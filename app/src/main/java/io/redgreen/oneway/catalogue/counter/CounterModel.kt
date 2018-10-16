package io.redgreen.oneway.catalogue.counter

import io.reactivex.Observable
import io.redgreen.oneway.SourceLifecycleEvent
import io.redgreen.oneway.catalogue.counter.usecases.CounterUseCases

object CounterModel {
  fun createSource(
      intentions: Observable<CounterIntention>,
      sourceLifecycleEvents: Observable<SourceLifecycleEvent>,
      useCases: CounterUseCases
  ): Observable<CounterState> {
    val incrementIntentions = intentions
        .ofType(Increment::class.java)
        .map { Unit }

    val decrementIntentions = intentions
        .ofType(Decrement::class.java)
        .map { Unit }

    return Observable.merge(
        sourceLifecycleEvents.compose(useCases.sourceCreatedUseCase),
        sourceLifecycleEvents.compose(useCases.sourceRestoredUseCase),
        incrementIntentions.compose(useCases.incrementUseCase),
        decrementIntentions.compose(useCases.decrementUseCase)
    )
  }
}
