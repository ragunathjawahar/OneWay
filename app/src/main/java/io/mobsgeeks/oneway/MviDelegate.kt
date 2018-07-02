package io.mobsgeeks.oneway

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

class MviDelegate<T> {
  private lateinit var disposable: Disposable

  fun setup(
      source: () -> Observable<T>,
      sink: (Observable<T>) -> Disposable
  ): Disposable { // TODO(rj) 2/Jul/18 - Ensure this disposable can't be unsubscribed.
    disposable = sink(source())
    return ReadOnlyDisposable(disposable)
  }

  fun teardown() {
    if (!disposable.isDisposed) {
      disposable.dispose()
    }
  }
}
