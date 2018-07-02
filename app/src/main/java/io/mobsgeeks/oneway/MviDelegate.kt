package io.mobsgeeks.oneway

import io.mobsgeeks.oneway.Binding.CREATED
import io.reactivex.BackpressureStrategy.LATEST
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlin.LazyThreadSafetyMode.NONE

class MviDelegate<T> {
  private lateinit var disposable: Disposable
  private val bindingsSubject = PublishSubject.create<Binding>()

  private val bindings by lazy(NONE) {
    bindingsSubject.toFlowable(LATEST).toObservable()
  }

  fun setup(
      source: (Observable<Binding>) -> Observable<T>,
      sink: (Observable<T>) -> Disposable
  ): Disposable {
    disposable = sink(source(bindings))
    bindingsSubject.onNext(CREATED)
    return ReadOnlyDisposable(disposable)
  }

  fun teardown() {
    if (!disposable.isDisposed) {
      disposable.dispose()
    }
  }
}
