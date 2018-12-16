package io.redgreen.oneway.catalogue.lotsofcounters

import io.reactivex.Observable
import io.reactivex.rxkotlin.withLatestFrom
import io.redgreen.oneway.SourceLifecycleEvent
import io.redgreen.oneway.usecases.SourceCreatedUseCase
import io.redgreen.oneway.usecases.SourceRestoredUseCase

object LotsOfCountersModel {
  fun createSource(
      intentions: Observable<LotsOfCountersIntention>,
      sourceLifecycleEvents: Observable<SourceLifecycleEvent>,
      sourceCopy: Observable<LotsOfCountersState>
  ): Observable<LotsOfCountersState> {
    val addCounterStates = intentions
        .ofType(AddCounterIntention::class.java)
        .withLatestFrom(sourceCopy) { _, state -> state.addCounter() }

    val incrementCounterStates = intentions
        .ofType(IncrementCounterAtIntention::class.java)
        .withLatestFrom(sourceCopy) { intention, state -> state.incrementCounter(intention.index) }

    val decrementCounterStates = intentions
        .ofType(DecrementCounterAtIntention::class.java)
        .withLatestFrom(sourceCopy) { intention, state -> state.decrementCounter(intention.index) }

    val removeCounterStates = intentions
        .ofType(RemoveCounterAtIntention::class.java)
        .withLatestFrom(sourceCopy) { intention, state -> state.removeCounter(intention.index) }

    return Observable.mergeArray(
        sourceLifecycleEvents.compose(SourceCreatedUseCase(LotsOfCountersState.NO_COUNTERS)),
        sourceLifecycleEvents.compose(SourceRestoredUseCase(sourceCopy)),
        addCounterStates,
        incrementCounterStates,
        decrementCounterStates,
        removeCounterStates
    )
  }
}
