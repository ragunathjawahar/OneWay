package io.mobsgeeks.oneway

import io.reactivex.Observable

interface Source<S> {
  fun produce(bindings: Observable<Binding>, timeline: Observable<S>): Observable<S>
}
