package io.mobsgeeks.oneway.drivers

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

interface ViewDriver<S> {
  fun render(source: Observable<S>): Disposable
}
