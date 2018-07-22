package io.mobsgeeks.oneway.test

import io.mobsgeeks.oneway.Binding
import io.mobsgeeks.oneway.Binding.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class MviTestRule<I> {
  private val bindingsSubject = PublishSubject.create<Binding>()

  val intentions = PublishSubject.create<I>()
  val bindings: Observable<Binding> = bindingsSubject
      .toFlowable(BackpressureStrategy.LATEST)
      .toObservable()

  fun screenIsCreated() {
    bindingsSubject.onNext(CREATED)
  }

  fun screenIsRestored() {
    bindingsSubject.onNext(RESTORED)
  }

  fun screenIsDestroyed() {
    bindingsSubject.onNext(DESTROYED)
  }
}
