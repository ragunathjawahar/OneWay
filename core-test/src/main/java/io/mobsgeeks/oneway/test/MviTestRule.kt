package io.mobsgeeks.oneway.test

import io.mobsgeeks.oneway.SourceEvent
import io.mobsgeeks.oneway.SourceEvent.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class MviTestRule<S>(private val sourceFunction: (Observable<SourceEvent>, Observable<S>) -> Observable<S>) {
  private val sourceEventsSubject = PublishSubject.create<SourceEvent>()
  private val sourceEvents: Observable<SourceEvent> = sourceEventsSubject.hide()

  private val timelineSubject = BehaviorSubject.create<S>()
  private val timeline: Observable<S> = timelineSubject.hide()

  private lateinit var internalTestObserver: TestObserver<S>
  val testObserver: TestObserver<S>
    get() = internalTestObserver

  private val compositeDisposable = CompositeDisposable()

  init {
    createSource(sourceFunction)
  }

  fun screenIsCreated() {
    createSource(sourceFunction)
    sourceEventsSubject.onNext(CREATED)
  }

  fun screenIsRestored() {
    createSource(sourceFunction)
    sourceEventsSubject.onNext(RESTORED)
  }

  fun screenIsDestroyed() {
    sourceEventsSubject.onNext(DESTROYED)
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

  private fun createSource(sourceFunction: (Observable<SourceEvent>, Observable<S>) -> Observable<S>) {
    val subscriptionExists = ::internalTestObserver.isInitialized
        && !internalTestObserver.isDisposed
    if (subscriptionExists) {
      return
    }

    internalTestObserver = TestObserver()
    val sharedStates = sourceFunction(sourceEvents, timeline).share()
    compositeDisposable.addAll(
        sharedStates.subscribe { timelineSubject.onNext(it) },
        sharedStates.subscribeWith(internalTestObserver)
    )
  }
}
