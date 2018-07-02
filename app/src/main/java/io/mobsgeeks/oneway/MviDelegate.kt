package io.mobsgeeks.oneway

import io.mobsgeeks.oneway.Binding.CREATED
import io.mobsgeeks.oneway.Binding.DESTROYED
import io.reactivex.BackpressureStrategy.LATEST
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlin.LazyThreadSafetyMode.NONE

class MviDelegate<T> {
  private lateinit var disposable: Disposable
  private val bindingsSubject = PublishSubject.create<Binding>()
  private val timelineSubject = PublishSubject.create<T>()

  val bindings: Observable<Binding> by lazy(NONE) {
    bindingsSubject.toFlowable(LATEST).toObservable()
  }

  internal val timeline: Observable<T> by lazy(NONE) {
    timelineSubject.toFlowable(LATEST).toObservable().share()
  }

  fun setup(
      source: (Observable<Binding>) -> Observable<T>,
      sink: (Observable<T>) -> Disposable
  ): Disposable {
    val sharedStates = source(bindings).share()
    disposable = sink(sharedStates)
    val timelineDisposable = sharedStates.subscribeWith(timelineSubject)
    bindingsSubject.onNext(CREATED)
    return ReadOnlyDisposable(disposable)
  }

  fun teardown() {
    if (::disposable.isInitialized && !disposable.isDisposed) {
      disposable.dispose()
      bindingsSubject.onNext(DESTROYED)
    }
  }
}
