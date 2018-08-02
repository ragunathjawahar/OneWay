package io.mobsgeeks.oneway.usecases

import io.mobsgeeks.oneway.SourceEvent
import io.mobsgeeks.oneway.SourceEvent.CREATED
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class DefaultSourceCreatedUseCase<T>(
    private val initialState: T
) : ObservableTransformer<SourceEvent, T> {
  override fun apply(sourceEvents: Observable<SourceEvent>): ObservableSource<T> {
    return sourceEvents
        .filter { it == CREATED }
        .map { initialState }
  }
}
