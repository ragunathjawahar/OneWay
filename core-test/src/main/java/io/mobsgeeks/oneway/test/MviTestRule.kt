package io.mobsgeeks.oneway.test

import io.mobsgeeks.oneway.SourceEvent
import io.mobsgeeks.oneway.SourceEvent.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

/**
 * A test rule that makes isolated testing MVI models more enjoyable.
 *
 * @param sourceFunction provides `sourceEvents` and `timeline` for the
 *                       model to create a source.
 */
class MviTestRule<S>(
    private val sourceFunction: (Observable<SourceEvent>, Observable<S>) -> Observable<S>
) {
  private val sourceEventsSubject = PublishSubject.create<SourceEvent>()
  private val sourceEvents: Observable<SourceEvent> = sourceEventsSubject.hide()

  private val timelineSubject = BehaviorSubject.create<S>()
  private val timeline: Observable<S> = timelineSubject.hide()

  private lateinit var internalTestObserver: TestObserver<S>

  private val compositeDisposable = CompositeDisposable()

  /** A test observer that is subscribed to the source. */
  val testObserver: TestObserver<S>
    get() = internalTestObserver

  init {
    createSource(sourceFunction)
  }

  /** Simulates the [SourceEvent.CREATED] event. */
  fun sourceIsCreated() {
    createSource(sourceFunction)
    sourceEventsSubject.onNext(CREATED)
  }

  /** Simulates the [SourceEvent.DESTROYED] event. */
  fun sourceIsDestroyed() {
    sourceEventsSubject.onNext(DESTROYED)
    compositeDisposable.clear()
  }

  /** Simulates the [SourceEvent.RESTORED] event. */
  fun sourceIsRestored() {
    createSource(sourceFunction)
    sourceEventsSubject.onNext(RESTORED)
  }

  /** Starts with the given [startState] and then executes the following [block]. */
  fun startWith(startState: S, block: () -> Unit) {
    timelineSubject.onNext(startState)
    block()
  }

  /** Asserts the [states] that were observed by the [testObserver]. */
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
