package io.redgreen.oneway.test

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.SourceLifecycleEvent
import io.redgreen.oneway.SourceLifecycleEvent.CREATED
import io.redgreen.oneway.SourceLifecycleEvent.RESTORED

/**
 * A test rule that makes isolated testing MVI models more enjoyable.
 *
 * @param sourceFunction provides `sourceLifecycleEvents` and `sourceCopy` for the
 *                       model to create a source.
 */
class MviTestHelper<S>(
    private val sourceFunction: (Observable<SourceLifecycleEvent>, Observable<S>) -> Observable<S>
) {
  private val sourceLifecycleEventsSubject = PublishSubject.create<SourceLifecycleEvent>()
  private val sourceLifecycleEvents: Observable<SourceLifecycleEvent> = sourceLifecycleEventsSubject.hide()

  private val sourceCopySubject = BehaviorSubject.create<S>()
  private val sourceCopy: Observable<S> = sourceCopySubject.hide()

  private lateinit var internalTestObserver: TestObserver<S>

  private val compositeDisposable = CompositeDisposable()

  /** A test observer that is subscribed to the source. */
  val testObserver: TestObserver<S>
    get() = internalTestObserver

  init {
    createSource(sourceFunction)
  }

  /** Simulates the [SourceLifecycleEvent.CREATED] event. */
  fun sourceIsCreated() {
    createSource(sourceFunction)
    sourceLifecycleEventsSubject.onNext(CREATED)
  }

  /** Destroys the source by disposing underlying subscriptions. */
  fun sourceIsDestroyed() {
    compositeDisposable.clear()
  }

  /** Simulates the [SourceLifecycleEvent.RESTORED] event. */
  fun sourceIsRestored() {
    createSource(sourceFunction)
    sourceLifecycleEventsSubject.onNext(RESTORED)
  }

  /** Starts with the given [startState] and then executes the following [block]. */
  fun setState(startState: S, block: () -> Unit = {}) {
    sourceCopySubject.onNext(startState)
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

  private fun createSource(sourceFunction: (Observable<SourceLifecycleEvent>, Observable<S>) -> Observable<S>) {
    val subscriptionExists = ::internalTestObserver.isInitialized
        && !internalTestObserver.isDisposed
    if (subscriptionExists) {
      return
    }

    internalTestObserver = TestObserver()
    val sharedStates = sourceFunction(sourceLifecycleEvents, sourceCopy).share()
    compositeDisposable.addAll(
        sharedStates.subscribe { sourceCopySubject.onNext(it) },
        sharedStates.subscribeWith(internalTestObserver)
    )
  }
}
