package io.mobsgeeks.oneway.android.barebones

import io.mobsgeeks.oneway.SourceEvent
import io.mobsgeeks.oneway.StateSerializer
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

interface AndroidMviContract<S, P> {
  val timeline: Observable<S>

  val stateSerializer: StateSerializer<S, P>

  val persister: Persister<P>

  fun source(
      sourceEvents: Observable<SourceEvent>,
      timeline: Observable<S>
  ): Observable<S>

  fun sink(source: Observable<S>): Disposable
}
