package io.mobsgeeks.oneway.usecases

import io.mobsgeeks.oneway.SourceEvent
import io.mobsgeeks.oneway.SourceEvent.RESTORED
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction

class SourceRestoredUseCase<T>(
    private val timeline: Observable<T>
) : ObservableTransformer<SourceEvent, T> {
  override fun apply(sourceEvents: Observable<SourceEvent>): Observable<T> {
    return sourceEvents
        .filter { it == RESTORED }
        .withLatestFrom(timeline, BiFunction { _, state -> state })
  }
}
