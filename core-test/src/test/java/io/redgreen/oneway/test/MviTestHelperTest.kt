package io.redgreen.oneway.test

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.observers.TestObserver
import io.redgreen.oneway.SourceLifecycleEvent
import io.redgreen.oneway.SourceLifecycleEvent.CREATED
import io.redgreen.oneway.SourceLifecycleEvent.RESTORED
import org.junit.jupiter.api.*

class MviTestHelperTest {
  @DisplayName("when source")
  @Nested inner class SourceLifecycleEventTest {
    private lateinit var testHelper: MviTestHelper<SomeState>
    private val sourceLifecycleEventsTestObserver = TestObserver<SourceLifecycleEvent>()
    private lateinit var sourceLifecycleEventsDisposable: Disposable

    @BeforeEach fun setUp() {
      testHelper = MviTestHelper { sourceLifecycleEvents, _ ->
        sourceLifecycleEventsDisposable = sourceLifecycleEvents.subscribeWith(sourceLifecycleEventsTestObserver)
        return@MviTestHelper Observable.never()
      }
    }

    @AfterEach fun tearDown() {
      sourceLifecycleEventsDisposable.dispose()
      testHelper.sourceIsDestroyed()
    }

    @Test fun `is created then emit CREATED`() {
      // when
      testHelper.sourceIsCreated()

      // then
      assertValue(sourceLifecycleEventsTestObserver, CREATED)
    }

    @Test fun `is restored then emit RESTORED`() {
      // when
      testHelper.sourceIsRestored()

      // then
      assertValue(sourceLifecycleEventsTestObserver, RESTORED)
    }
  }

  private val testHelper = MviTestHelper<SomeState> { _, _ -> Observable.never() }

  @Test fun `it can setup a start state`() {
    // given
    val startState = SomeState("Start")
    val sourceCopyTestObserver = TestObserver<SomeState>()
    var sourceCopyDisposable: Disposable? = null

    val testHelper = MviTestHelper<SomeState> { _, sourceCopy ->
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
    val sourceFunction = mock<(Observable<SourceLifecycleEvent>, Observable<SomeState>) -> Observable<SomeState>>()
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
    val testHelper = MviTestHelper<SomeState> { sourceLifecycleEvents, _ ->
      return@MviTestHelper sourceLifecycleEvents.flatMap { Observable.just(stateA, stateB) }
    }

    // when
    testHelper.sourceIsCreated()

    // then
    testHelper.assertStates(stateA, stateB)
  }

  @Test fun `it can assert no states`() {
    val testHelper = MviTestHelper<SomeState> { _, _ ->
      Observable.never<SomeState>()
    }

    // when
    testHelper.sourceIsCreated()

    // then
    testHelper.assertNoStates()
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
    val testHelper = MviTestHelper<SomeState> { sourceLifecycleEvents, sourceCopy ->
      val sourceCreatedUseCaseStates = sourceLifecycleEvents
          .filter { it == CREATED }
          .map { oneState }

      val combiner = BiFunction<SourceLifecycleEvent, SomeState, SomeState> { _, state -> state }
      val sourceRestoredUseCaseStates = sourceLifecycleEvents
          .filter { it == RESTORED }
          .withLatestFrom(sourceCopy, combiner)

      return@MviTestHelper Observable
          .merge(sourceCreatedUseCaseStates, sourceRestoredUseCaseStates)
    }

    // when
    testHelper.sourceIsCreated()
    testHelper.assertStates(oneState)
    val testObserverAfterCreated = testHelper.testObserver

    testHelper.sourceIsDestroyed()
    testHelper.sourceIsRestored()
    val testObserverAfterRestored = testHelper.testObserver

    // then
    testHelper.assertStates(oneState)
    assertThat(testObserverAfterRestored)
        .isNotSameAs(testObserverAfterCreated)
  }

  private fun <T> assertValue(
      sourceLifecycleEventsTestObserver: TestObserver<T>,
      sourceEvent: T
  ) {
    with(sourceLifecycleEventsTestObserver) {
      assertNoErrors()
      assertValues(sourceEvent)
      assertNotTerminated()
    }
  }
}

data class SomeState(val message: String)
