package io.mobsgeeks.oneway

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

class MviDelegate<T> {
  fun setup(
      sourceFunction: () -> Observable<T>,
      sinkFunction: (Observable<T>) -> Disposable
  ): Disposable { // TODO(rj) 2/Jul/18 - Ensure this disposable can't be unsubscribed.
    return sinkFunction(sourceFunction())
  }
}
