package io.mobsgeeks.oneway

import io.mobsgeeks.oneway.Binding.CREATED
import io.mobsgeeks.oneway.Binding.DESTROYED
import io.reactivex.BackpressureStrategy.LATEST
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlin.LazyThreadSafetyMode.NONE

class MviDelegate<S, P> {
  private val compositeDisposable = CompositeDisposable()
  private val bindingsSubject = PublishSubject.create<Binding>()
  private val timelineSubject = PublishSubject.create<S>()

  val bindings: Observable<Binding> by lazy(NONE) {
    bindingsSubject.toFlowable(LATEST).toObservable()
  }

  internal val timeline: Observable<S> by lazy(NONE) {
    timelineSubject.toFlowable(LATEST).toObservable().share()
  }

  fun setup(
      source: (Observable<Binding>, Observable<S>) -> Observable<S>,
      sink: (Observable<S>) -> Disposable
  ) {
    val sharedStates = source(bindings, timeline).share()
    compositeDisposable.addAll(
        sink(sharedStates),
        sharedStates.subscribe { timelineSubject.onNext(it) }
    )
    bindingsSubject.onNext(CREATED)
  }

  fun teardown() {
    if (compositeDisposable.size() > 0) {
      compositeDisposable.clear()
      bindingsSubject.onNext(DESTROYED)
    }
  }

  fun saveState(): P? {
    return null
  }
}
