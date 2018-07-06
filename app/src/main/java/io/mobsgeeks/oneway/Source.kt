package io.mobsgeeks.oneway

import io.reactivex.Observable

interface Source<S> {
  fun produce(bindings: Observable<Binding>, states: Observable<S>): Observable<S>
}
