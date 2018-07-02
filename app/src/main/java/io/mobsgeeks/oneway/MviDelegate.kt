package io.mobsgeeks.oneway

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

class MviDelegate<T> {
  private lateinit var disposable: Disposable

  fun setup(
      sourceFunction: () -> Observable<T>,
      sinkFunction: (Observable<T>) -> Disposable
  ): Disposable { // TODO(rj) 2/Jul/18 - Ensure this disposable can't be unsubscribed.
    disposable = sinkFunction(sourceFunction())
    return disposable
  }

  fun teardown() {
    if (!disposable.isDisposed) {
      disposable.dispose()
    }
  }
}
