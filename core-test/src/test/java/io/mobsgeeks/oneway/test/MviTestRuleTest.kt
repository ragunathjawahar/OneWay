package io.mobsgeeks.oneway.test

import com.google.common.truth.Truth.assertThat
import io.reactivex.Observable
import org.junit.Test

class MviTestRuleTest {
  private val testRule = MviTestRule<Intention>()

  @Test
  fun `it exposes an intentions subject`() {
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

  @Test
  fun `it exposes a bindings observable`() {
    assertThat(testRule.bindings)
        .isInstanceOf(Observable::class.java)
  }
}

object Intention
