package io.mobsgeeks.oneway.test

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockito_kotlin.*
import io.mobsgeeks.oneway.Binding
import io.mobsgeeks.oneway.Binding.*
import io.reactivex.Observable
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

  @Test fun `it exposes a timeline observable`() {
    assertThat(testRule.timeline)
        .isInstanceOf(Observable::class.java)
  }

  @Test fun `it can setup a start state`() {
    // given
    val startState = SomeState("Start")
    val testObserver = testRule.timeline.test()

    // when
    testRule.startWith(startState) {}

    // then
    testObserver.assertValue(startState)
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

  @Test fun `it can invoke a binding function to setup subscription`() {
    // given
    val bindingFunction = mock<(Observable<Binding>, Observable<SomeState>) -> Observable<SomeState>>()
    whenever(bindingFunction(any(), any()))
        .thenReturn(Observable.never())

    // when
    MviTestRule(bindingFunction).testObserver.hasSubscription()

    // then
    verify(bindingFunction).invoke(any(), any())
    verifyNoMoreInteractions(bindingFunction)
  }

  @Test fun `it can setup subscription with a test observer`() {
    // then
    val activeSubscription = testRule.testObserver.hasSubscription()
    assertThat(activeSubscription)
        .isTrue()
  }

  @Test fun `it can setup subscription with the timeline`() {
    // given
    val stateA = SomeState("A")
    val stateB = SomeState("B")
    val bindingFunction = { bindings: Observable<Binding>, _: Observable<SomeState> ->
      bindings.flatMap { Observable.just(stateA, stateB) }
    }
    val mviTestRule = MviTestRule(bindingFunction)
    val testObserver = mviTestRule.timeline.test()

    // when
    mviTestRule.screenIsCreated()

    // then
    testObserver.assertValues(stateA, stateB)
  }

  // TODO(rj) It should dispose subscription on screenIsDestroyed()
}

data class SomeState(val message: String)
