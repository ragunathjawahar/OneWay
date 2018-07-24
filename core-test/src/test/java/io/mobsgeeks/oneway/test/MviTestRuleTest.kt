package io.mobsgeeks.oneway.test

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockito_kotlin.*
import io.mobsgeeks.oneway.Binding
import io.mobsgeeks.oneway.Binding.*
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.observers.TestObserver
import org.junit.Test

class MviTestRuleTest {
  private val testRule = MviTestRule<SomeState> { _, _ -> Observable.never() }

  @Test fun `it exposes a bindings observable`() {
    assertThat(testRule.bindings)
        .isInstanceOf(Observable::class.java)
  }

  @Test fun `it emits CREATED when the screen is created`() {
    // given
    val testObserver = testRule.bindings.test()

    // when
    testRule.screenIsCreated()

    // then
    with(testObserver) {
      assertNoErrors()
      assertValues(CREATED)
      assertNotTerminated()
    }
  }

  @Test fun `it emits RESTORED when the screen is restored`() {
    // given
    val testObserver = testRule.bindings.test()

    // when
    testRule.screenIsRestored()

    // then
    with(testObserver) {
      assertNoErrors()
      assertValues(RESTORED)
      assertNotTerminated()
    }
  }

  @Test fun `it emits DESTROYED when the screen is destroyed`() {
    // given
    val testObserver = testRule.bindings.test()

    // when
    testRule.screenIsDestroyed()

    // then
    with(testObserver) {
      assertNoErrors()
      assertValues(DESTROYED)
      assertNotTerminated()
    }
  }

  @Test fun `it can setup a start state`() {
    // given
    val startState = SomeState("Start")
    val timelineTestObserver = TestObserver<SomeState>()
    var timelineDisposable: Disposable? = null

    val testRule = MviTestRule { _: Observable<Binding>, timeline: Observable<SomeState> ->
      timelineDisposable = timeline.subscribeWith(timelineTestObserver)
      return@MviTestRule Observable.never()
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
    testRule.startWith(startState, block)

    // then
    verify(block).invoke()
    verifyNoMoreInteractions(block)
  }

  @Test fun `it can invoke a source function to setup subscription`() {
    // given
    val sourceFunction = mock<(Observable<Binding>, Observable<SomeState>) -> Observable<SomeState>>()
    whenever(sourceFunction(any(), any()))
        .thenReturn(Observable.never())

    // when
    MviTestRule(sourceFunction).testObserver.hasSubscription()

    // then
    verify(sourceFunction).invoke(any(), any())
    verifyNoMoreInteractions(sourceFunction)
  }

  @Test fun `it can setup subscription with a test observer`() {
    // then
    val activeSubscription = testRule.testObserver.hasSubscription()
    assertThat(activeSubscription)
        .isTrue()
    assertThat(testRule.testObserver.isDisposed)
        .isFalse()
  }

  @Test fun `it can assert states`() {
    // given
    val stateA = SomeState("A")
    val stateB = SomeState("B")
    val sourceFunction = { bindings: Observable<Binding>, _: Observable<SomeState> ->
      bindings.flatMap { Observable.just(stateA, stateB) }
    }
    val mviTestRule = MviTestRule(sourceFunction)

    // when
    mviTestRule.screenIsCreated()

    // then
    mviTestRule.assertStates(stateA, stateB)
  }

  // TODO(rj) 24/Jun/18 - Ensure no state emission happens after the subscription is disposed.
  @Test fun `it disposes subscriptions when the screen is destroyed`() {
    // when
    testRule.screenIsDestroyed()

    // then
    assertThat(testRule.testObserver.isDisposed)
        .isTrue()
  }

  @Test fun `it restores subscriptions when the screen is restored`() {
    // given
    testRule.screenIsDestroyed()

    // when
    testRule.screenIsRestored()

    // then
    assertThat(testRule.testObserver.hasSubscription())
        .isTrue()
    assertThat(testRule.testObserver.isDisposed)
        .isFalse()
  }

  @Test fun `it restores subscriptions when the screen is created`() {
    // given
    testRule.screenIsDestroyed()

    // when
    testRule.screenIsCreated()

    // then
    assertThat(testRule.testObserver.hasSubscription())
        .isTrue()
    assertThat(testRule.testObserver.isDisposed)
        .isFalse()
  }

  @Test fun `it has access to the last known state after the screen is restored`() {
    // given
    val oneState = SomeState("ONE")
    val sourceFunction = { bindings: Observable<Binding>, timeline: Observable<SomeState> ->
      val bindingCreatedStates = bindings
          .filter { it == CREATED }
          .map { oneState }

      val combiner = BiFunction<Binding, SomeState, SomeState> { _, state -> state }
      val bindingRestoredStates = bindings
          .filter { it == RESTORED }
          .withLatestFrom(timeline, combiner)

      Observable.merge(bindingCreatedStates, bindingRestoredStates)
    }
    val mviTestRule = MviTestRule(sourceFunction)
    mviTestRule.screenIsCreated()
    mviTestRule.screenIsDestroyed()
    mviTestRule.assertStates(oneState)

    // when
    mviTestRule.screenIsRestored()

    // then
    mviTestRule.assertStates(oneState)
  }
}

data class SomeState(val message: String)
