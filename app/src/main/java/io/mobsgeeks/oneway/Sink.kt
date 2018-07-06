package io.mobsgeeks.oneway

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

interface Sink<S> {
  fun consume(source: Observable<S>): Disposable
}
