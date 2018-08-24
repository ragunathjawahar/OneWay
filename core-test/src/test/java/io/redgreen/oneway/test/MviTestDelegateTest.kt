package io.redgreen.oneway.test

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.observers.TestObserver
import io.redgreen.oneway.SourceEvent
import io.redgreen.oneway.SourceEvent.*
import org.junit.Test

class MviTestDelegateTest {
  private val testDelegate = MviTestDelegate<SomeState> { _, _ -> Observable.never() }

  @Test fun `it emits CREATED when the screen is created`() {
    // given
    val sourceEventsTestObserver = TestObserver<SourceEvent>()
    var sourceEventsDisposable: Disposable? = null

    val testRule = MviTestDelegate { sourceEvents: Observable<SourceEvent>, _: Observable<SomeState> ->
      sourceEventsDisposable = sourceEvents.subscribeWith(sourceEventsTestObserver)
      return@MviTestDelegate Observable.never()
    }

    // when
    testRule.sourceIsCreated()

    // then
    with(sourceEventsTestObserver) {
      assertNoErrors()
      assertValues(CREATED)
      assertNotTerminated()
    }

    sourceEventsDisposable?.dispose()
  }

  @Test fun `it emits RESTORED when the screen is restored`() {
    val sourceEventsTestObserver = TestObserver<SourceEvent>()
    var sourceEventsDisposable: Disposable? = null

    val testRule = MviTestDelegate { sourceEvents: Observable<SourceEvent>, _: Observable<SomeState> ->
      sourceEventsDisposable = sourceEvents.subscribeWith(sourceEventsTestObserver)
      return@MviTestDelegate Observable.never()
    }

    // when
    testRule.sourceIsRestored()

    // then
    with(sourceEventsTestObserver) {
      assertNoErrors()
      assertValues(RESTORED)
      assertNotTerminated()
    }

    sourceEventsDisposable?.dispose()
  }

  @Test fun `it emits DESTROYED when the screen is destroyed`() {
    val sourceEventsTestObserver = TestObserver<SourceEvent>()
    var sourceEventsDisposable: Disposable? = null

    val testRule = MviTestDelegate { sourceEvents: Observable<SourceEvent>, _: Observable<SomeState> ->
      sourceEventsDisposable = sourceEvents.subscribeWith(sourceEventsTestObserver)
      return@MviTestDelegate Observable.never()
    }

    // when
    testRule.sourceIsDestroyed()

    // then
    with(sourceEventsTestObserver) {
      assertNoErrors()
      assertValues(DESTROYED)
      assertNotTerminated()
    }

    sourceEventsDisposable?.dispose()
  }

  @Test fun `it can setup a start state`() {
    // given
    val startState = SomeState("Start")
    val timelineTestObserver = TestObserver<SomeState>()
    var timelineDisposable: Disposable? = null

    val testRule = MviTestDelegate { _: Observable<SourceEvent>, timeline: Observable<SomeState> ->
      timelineDisposable = timeline.subscribeWith(timelineTestObserver)
      return@MviTestDelegate Observable.never()
    }

    // when
    testRule.startWith(startState) { /* this block is intentionally left blank */ }

    // then
    with(timelineTestObserver) {
      assertNoErrors()
      assertValue(startState)
      assertNotTerminated()
    }
    timelineDisposable?.dispose()
  }

  @Test fun `it can invoke a block after setting up a start state`() {
    // given
    val startState = SomeState("Start")
    val block = mock<() -> Unit>{}

    // when
    testDelegate.startWith(startState, block)

    // then
    verify(block).invoke()
    verifyNoMoreInteractions(block)
  }

  @Test fun `it can invoke a source function to setup subscription`() {
    // given
    val sourceFunction = mock<(Observable<SourceEvent>, Observable<SomeState>) -> Observable<SomeState>>()
    whenever(sourceFunction(any(), any()))
        .thenReturn(Observable.never())

    // when
    MviTestDelegate(sourceFunction).testObserver.hasSubscription()

    // then
    verify(sourceFunction).invoke(any(), any())
    verifyNoMoreInteractions(sourceFunction)
  }

  @Test fun `it can setup subscription with a test observer`() {
    // then
    val activeSubscription = testDelegate.testObserver.hasSubscription()
    assertThat(activeSubscription)
        .isTrue()
    assertThat(testDelegate.testObserver.isDisposed)
        .isFalse()
  }

  @Test fun `it can assert states`() {
    // given
    val stateA = SomeState("A")
    val stateB = SomeState("B")
    val sourceFunction = { sourceEvents: Observable<SourceEvent>, _: Observable<SomeState> ->
      sourceEvents.flatMap { Observable.just(stateA, stateB) }
    }
    val mviTestDelegate = MviTestDelegate(sourceFunction)

    // when
    mviTestDelegate.sourceIsCreated()

    // then
    mviTestDelegate.assertStates(stateA, stateB)
  }

  // TODO(rj) 24/Jun/18 - Ensure no state emission happens after the subscription is disposed.
  @Test fun `it disposes subscriptions when the screen is destroyed`() {
    // when
    testDelegate.sourceIsDestroyed()

    // then
    assertThat(testDelegate.testObserver.isDisposed)
        .isTrue()
  }

  @Test fun `it restores subscriptions when the screen is restored`() {
    // given
    testDelegate.sourceIsDestroyed()

    // when
    testDelegate.sourceIsRestored()

    // then
    assertThat(testDelegate.testObserver.hasSubscription())
        .isTrue()
    assertThat(testDelegate.testObserver.isDisposed)
        .isFalse()
  }

  @Test fun `it restores subscriptions when the screen is created`() {
    // given
    testDelegate.sourceIsDestroyed()

    // when
    testDelegate.sourceIsCreated()

    // then
    assertThat(testDelegate.testObserver.hasSubscription())
        .isTrue()
    assertThat(testDelegate.testObserver.isDisposed)
        .isFalse()
  }

  @Test fun `it has access to the last known state after the screen is restored`() {
    // given
    val oneState = SomeState("ONE")
    val sourceFunction = { sourceEvents: Observable<SourceEvent>, timeline: Observable<SomeState> ->
      val sourceCreatedUseCaseStates = sourceEvents
          .filter { it == CREATED }
          .map { oneState }

      val combiner = BiFunction<SourceEvent, SomeState, SomeState> { _, state -> state }
      val sourceRestoredUseCaseStates = sourceEvents
          .filter { it == RESTORED }
          .withLatestFrom(timeline, combiner)

      Observable.merge(sourceCreatedUseCaseStates, sourceRestoredUseCaseStates)
    }
    val mviTestDelegate = MviTestDelegate(sourceFunction)

    // when
    mviTestDelegate.sourceIsCreated()
    mviTestDelegate.assertStates(oneState)
    val testObserverAfterCreated = mviTestDelegate.testObserver

    mviTestDelegate.sourceIsDestroyed()
    mviTestDelegate.sourceIsRestored()
    val testObserverAfterRestored = mviTestDelegate.testObserver

    // then
    mviTestDelegate.assertStates(oneState)
    assertThat(testObserverAfterRestored)
        .isNotSameAs(testObserverAfterCreated)
  }
}

data class SomeState(val message: String)
