package io.mobsgeeks.oneway.test

import org.junit.Test

class MviTestRuleTest {
  @Test
  fun `it exposes an intentions subject`() {
    // given
    val testRule = MviTestRule<Intention>()
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
}

object Intention
