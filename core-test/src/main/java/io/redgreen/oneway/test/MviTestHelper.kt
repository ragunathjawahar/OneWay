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
 */
class MviTestHelper<S> {
  private val sourceLifecycleEventsSubject = PublishSubject.create<SourceLifecycleEvent>()
  private val sourceLifecycleEvents: Observable<SourceLifecycleEvent> = sourceLifecycleEventsSubject.hide()

  private val sourceCopySubject = BehaviorSubject.create<S>()
  private val sourceCopy: Observable<S> = sourceCopySubject.hide()

  private lateinit var sourceFunction: (Observable<SourceLifecycleEvent>, Observable<S>) -> Observable<S>

  private lateinit var internalTestObserver: TestObserver<S>

  private val compositeDisposable = CompositeDisposable()

  /** A test observer that is subscribed to the source. */
  val testObserver: TestObserver<S>
    get() {
      checkIfSourceIsSet()
      return internalTestObserver
    }

  /**
   * Set a source for the test helper.
   *
   * @param sourceFunction provides `sourceLifecycleEvents` and `sourceCopy` for the model to create a source.
   */
  fun setSource(sourceFunction: (Observable<SourceLifecycleEvent>, Observable<S>) -> Observable<S>) {
    checkIfSourceIsBeingSetAgain()
    this.sourceFunction = sourceFunction
    createSource(sourceFunction)
  }

  /** Simulates the [SourceLifecycleEvent.CREATED] event. */
  fun sourceIsCreated() {
    checkIfSourceIsSet()
    createSource(sourceFunction)
    sourceLifecycleEventsSubject.onNext(CREATED)
  }

  /** Destroys the source by disposing underlying subscriptions. */
  fun sourceIsDestroyed() {
    checkIfSourceIsSet()
    compositeDisposable.clear()
  }

  /** Simulates the [SourceLifecycleEvent.RESTORED] event. */
  fun sourceIsRestored() {
    checkIfSourceIsSet()
    createSource(sourceFunction)
    sourceLifecycleEventsSubject.onNext(RESTORED)
  }

  /** Starts with the given [startState] and then executes the following [block]. */
  fun setState(startState: S, block: () -> Unit = {}) {
    checkIfSourceIsSet()
    sourceCopySubject.onNext(startState)
    block()
  }

  /** Asserts the [states] that were observed by the [testObserver]. */
  fun assertStates(state: S, vararg states: S) {
    checkIfSourceIsSet()
    with(testObserver) {
      assertNoErrors()
      assertValues(state, *states)
      assertNotTerminated()
    }
  }

  /** Asserts that no state was observed by the [testObserver]. */
  fun assertNoStates() {
    checkIfSourceIsSet()
    with(testObserver) {
      assertNoErrors()
      assertNoValues()
      assertNotTerminated()
    }
  }

  private fun checkIfSourceIsSet() {
    if (!::sourceFunction.isInitialized) {
      throw IllegalStateException("Please set a source by calling the `setSource` function.")
    }
  }

  private fun checkIfSourceIsBeingSetAgain() {
    if (::sourceFunction.isInitialized) {
      throw IllegalStateException("Source is already set, please use a different instance of `MviTestHelper`.")
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
