package io.mobsgeeks.oneway.test

import io.mobsgeeks.oneway.Binding
import io.mobsgeeks.oneway.Binding.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class MviTestRule<S>(private val sourceFunction: (Observable<Binding>, Observable<S>) -> Observable<S>) {
  private val bindingsSubject = PublishSubject.create<Binding>()
  private val bindings: Observable<Binding> = bindingsSubject
      .toFlowable(BackpressureStrategy.LATEST)
      .toObservable()

  private val timelineSubject  = BehaviorSubject.create<S>()
  private val timeline: Observable<S> = timelineSubject
      .toFlowable(BackpressureStrategy.LATEST)
      .toObservable()

  private lateinit var internalTestObserver: TestObserver<S>
  val testObserver: TestObserver<S>
    get() = internalTestObserver

  private val compositeDisposable = CompositeDisposable()

  init {
    createSource(sourceFunction)
  }

  fun screenIsCreated() {
    createSource(sourceFunction)
    bindingsSubject.onNext(CREATED)
  }

  fun screenIsRestored() {
    createSource(sourceFunction)
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

  private fun createSource(sourceFunction: (Observable<Binding>, Observable<S>) -> Observable<S>) {
    val subscriptionExists = ::internalTestObserver.isInitialized
        && !internalTestObserver.isDisposed
    if (subscriptionExists) {
      return
    }

    internalTestObserver = TestObserver()
    val sharedStates = sourceFunction(bindings, timeline).share()
    compositeDisposable.addAll(
        sharedStates.subscribe { timelineSubject.onNext(it) },
        sharedStates.subscribeWith(internalTestObserver)
    )
  }
}
