package io.mobsgeeks.oneway

import io.reactivex.Observable

interface Source<S> {
  fun produce(sourceEvents: Observable<SourceEvent>, timeline: Observable<S>): Observable<S>
}
