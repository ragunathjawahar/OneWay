package io.mobsgeeks.oneway.test

import io.mobsgeeks.oneway.Binding
import io.mobsgeeks.oneway.Binding.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class MviTestRule<S>(bindingFunction: (Observable<Binding>, Observable<S>) -> Observable<S>) {
  private val bindingsSubject = PublishSubject.create<Binding>()
  val bindings: Observable<Binding> = bindingsSubject
      .toFlowable(BackpressureStrategy.LATEST)
      .toObservable()

  private val timelineSubject  = PublishSubject.create<S>()
  val timeline: Observable<S> = timelineSubject
      .toFlowable(BackpressureStrategy.LATEST)
      .toObservable()

  init {
    bindingFunction(bindings, timeline)
  }

  fun screenIsCreated() {
    bindingsSubject.onNext(CREATED)
  }

  fun screenIsRestored() {
    bindingsSubject.onNext(RESTORED)
  }

  fun screenIsDestroyed() {
    bindingsSubject.onNext(DESTROYED)
  }

  fun startWith(startState: S, block: () -> Unit) {
    timelineSubject.onNext(startState)
    block()
  }
}
