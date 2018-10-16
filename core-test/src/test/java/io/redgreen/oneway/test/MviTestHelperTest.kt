package io.redgreen.oneway.test

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.observers.TestObserver
import io.redgreen.oneway.SourceEvent
import io.redgreen.oneway.SourceEvent.CREATED
import io.redgreen.oneway.SourceEvent.RESTORED
import org.junit.jupiter.api.*

class MviTestHelperTest {
  @DisplayName("when source")
  @Nested inner class SourceEventTest {
    private lateinit var testHelper: MviTestHelper<SomeState>
    private val sourceEventsTestObserver = TestObserver<SourceEvent>()
    private lateinit var sourceEventsDisposable: Disposable

    @BeforeEach fun setUp() {
      testHelper = MviTestHelper { sourceEvents: Observable<SourceEvent>, _: Observable<SomeState> ->
        sourceEventsDisposable = sourceEvents.subscribeWith(sourceEventsTestObserver)
        return@MviTestHelper Observable.never()
      }
    }

    @AfterEach fun tearDown() {
      sourceEventsDisposable.dispose()
      testHelper.sourceIsDestroyed()
    }

    @Test fun `is created then emit CREATED`() {
      // when
      testHelper.sourceIsCreated()

      // then
      assertValue(sourceEventsTestObserver, CREATED)
    }

    @Test fun `is restored then emit RESTORED`() {
      // when
      testHelper.sourceIsRestored()

      // then
      assertValue(sourceEventsTestObserver, RESTORED)
    }
  }

  private val testHelper = MviTestHelper<SomeState> { _, _ -> Observable.never() }

  @Test fun `it can setup a start state`() {
    // given
    val startState = SomeState("Start")
    val sourceCopyTestObserver = TestObserver<SomeState>()
    var sourceCopyDisposable: Disposable? = null

    val testHelper = MviTestHelper { _: Observable<SourceEvent>, sourceCopy: Observable<SomeState> ->
      sourceCopyDisposable = sourceCopy.subscribeWith(sourceCopyTestObserver)
      return@MviTestHelper Observable.never()
    }

    // when
    testHelper.setState(startState) { /* this block is intentionally left blank */ }

    // then
    assertValue(sourceCopyTestObserver, startState)
    sourceCopyDisposable?.dispose()
  }

  @Test fun `it can invoke a block after setting up a start state`() {
    // given
    val startState = SomeState("Start")
    val block = mock<() -> Unit>{}

    // when
    testHelper.setState(startState, block)

    // then
    verify(block, times(1)).invoke()
  }

  @Test fun `it can invoke a source function to setup subscription`() {
    // given
    val sourceFunction = mock<(Observable<SourceEvent>, Observable<SomeState>) -> Observable<SomeState>>()
    whenever(sourceFunction(any(), any()))
        .thenReturn(Observable.never())

    // when
    MviTestHelper(sourceFunction).testObserver.hasSubscription()

    // then
    verify(sourceFunction).invoke(any(), any())
    verifyNoMoreInteractions(sourceFunction)
  }

  @Test fun `it can setup subscription with a test observer`() {
    // then
    val activeSubscription = testHelper.testObserver.hasSubscription()
    assertThat(activeSubscription)
        .isTrue()
    assertThat(testHelper.testObserver.isDisposed)
        .isFalse()
  }

  @Test fun `it can assert states`() {
    // given
    val stateA = SomeState("A")
    val stateB = SomeState("B")
    val sourceFunction = { sourceEvents: Observable<SourceEvent>, _: Observable<SomeState> ->
      sourceEvents.flatMap { Observable.just(stateA, stateB) }
    }
    val mviTestDelegate = MviTestHelper(sourceFunction)

    // when
    mviTestDelegate.sourceIsCreated()

    // then
    mviTestDelegate.assertStates(stateA, stateB)
  }

  // TODO(rj) 24/Jun/18 - Ensure no state emission happens after the subscription is disposed.
  @Test fun `it disposes subscriptions when the source is destroyed`() {
    // when
    testHelper.sourceIsDestroyed()

    // then
    assertThat(testHelper.testObserver.isDisposed)
        .isTrue()
  }

  @Test fun `it restores subscriptions when the source is restored`() {
    // given
    testHelper.sourceIsDestroyed()

    // when
    testHelper.sourceIsRestored()

    // then
    val testObserver = testHelper.testObserver

    assertThat(testObserver.hasSubscription())
        .isTrue()
    assertThat(testObserver.isDisposed)
        .isFalse()
  }

  @Test fun `it restores subscriptions when the source is created`() {
    // given
    testHelper.sourceIsDestroyed()

    // when
    testHelper.sourceIsCreated()

    // then
    val testObserver = testHelper.testObserver

    assertThat(testObserver.hasSubscription())
        .isTrue()
    assertThat(testObserver.isDisposed)
        .isFalse()
  }

  @Test fun `it has access to the last known state after the source is restored`() {
    // given
    val oneState = SomeState("ONE")
    val sourceFunction = { sourceEvents: Observable<SourceEvent>, sourceCopy: Observable<SomeState> ->
      val sourceCreatedUseCaseStates = sourceEvents
          .filter { it == CREATED }
          .map { oneState }

      val combiner = BiFunction<SourceEvent, SomeState, SomeState> { _, state -> state }
      val sourceRestoredUseCaseStates = sourceEvents
          .filter { it == RESTORED }
          .withLatestFrom(sourceCopy, combiner)

      Observable.merge(sourceCreatedUseCaseStates, sourceRestoredUseCaseStates)
    }
    val mviTestDelegate = MviTestHelper(sourceFunction)

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

  private fun <T> assertValue(
      sourceEventsTestObserver: TestObserver<T>,
      sourceEvent: T
  ) {
    with(sourceEventsTestObserver) {
      assertNoErrors()
      assertValues(sourceEvent)
      assertNotTerminated()
    }
  }
}

data class SomeState(val message: String)
