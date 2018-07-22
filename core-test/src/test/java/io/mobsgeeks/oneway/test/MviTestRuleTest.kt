package io.mobsgeeks.oneway.test

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.mobsgeeks.oneway.Binding.*
import io.reactivex.Observable
import org.junit.Test

class MviTestRuleTest {
  private val testRule = MviTestRule<Intention, State>()

  @Test fun `it exposes an intentions subject`() {
    // given
    val testObserver = testRule.intentions.test()

    // when
    testRule.intentions.onNext(Intention)

    // then
    with(testObserver) {
      assertNoErrors()
      assertValues(Intention)
      assertNotTerminated()
    }
  }

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
    val startState = State("Start")
    val testObserver = testRule.timeline.test()

    // when
    testRule.startWith(startState) {}

    // then
    testObserver.assertValue(startState)
  }

  @Test fun `it can invoke a block after setting up a start state`() {
    // given
    val startState = State("Start")
    val block = mock<() -> Unit>{}

    // when
    testRule.startWith(startState, block)

    // then
    verify(block).invoke()
  }
}

object Intention
data class State(val message: String)
