package io.mobsgeeks.oneway.test

import io.mobsgeeks.oneway.Binding
import io.mobsgeeks.oneway.Binding.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject

class MviTestRule<S>(bindingFunction: (Observable<Binding>, Observable<S>) -> Observable<S>) {
  private val bindingsSubject = PublishSubject.create<Binding>()
  val bindings: Observable<Binding> = bindingsSubject
      .toFlowable(BackpressureStrategy.LATEST)
      .toObservable() // TODO(rj) Do not expose bindings.

  private val timelineSubject  = PublishSubject.create<S>()
  val timeline: Observable<S> = timelineSubject
      .toFlowable(BackpressureStrategy.LATEST)
      .toObservable() // TODO(rj) Do not expose timeline.

  val testObserver = TestObserver<S>()

  private val compositeDisposable = CompositeDisposable()

  init {
    val sharedStates = bindingFunction(bindings, timeline).share()
    compositeDisposable.addAll(
        sharedStates.subscribe { timelineSubject.onNext(it) },
        sharedStates.subscribeWith(testObserver)
    )
  }

  fun screenIsCreated() {
    bindingsSubject.onNext(CREATED)
  }

  fun screenIsRestored() {
    bindingsSubject.onNext(RESTORED)
  }

  fun screenIsDestroyed() {
    bindingsSubject.onNext(DESTROYED)
    compositeDisposable.clear()
  }

  fun startWith(startState: S, block: () -> Unit) {
    timelineSubject.onNext(startState)
    block()
  }

  fun assertStates(vararg states: S) {
    with(testObserver) {
      assertNoErrors()
      assertValues(*states)
      assertNotTerminated()
    }
  }
}
