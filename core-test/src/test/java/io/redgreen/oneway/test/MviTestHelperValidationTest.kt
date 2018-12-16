package io.redgreen.oneway.test

import io.reactivex.Observable
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MviTestHelperValidationTest {
  private val mviTestHelper = MviTestHelper<SomeState>()

  @Test fun `when source is not set, then throw an exception if 'sourceIsCreated' is called`() {
    assertThrows<IllegalStateException> {
      mviTestHelper.sourceIsCreated()
    }
  }

  @Test fun `when source is not set, then throw an exception if 'sourceIsDestroyed' is called`() {
    assertThrows<IllegalStateException> {
      mviTestHelper.sourceIsDestroyed()
    }
  }

  @Test fun `when source is not set, then throw an exception if 'sourceIsRestored' is called`() {
    assertThrows<IllegalStateException> {
      mviTestHelper.sourceIsRestored()
    }
  }

  @Test fun `when source is not set, then throw an exception if 'assertStates' is called`() {
    assertThrows<IllegalStateException> {
      mviTestHelper.assertStates(SomeState("with a message"))
    }
  }

  @Test fun `when source is not set, then throw an exception if 'assertNoStates' is called`() {
    assertThrows<IllegalStateException> {
      mviTestHelper.assertNoStates()
    }
  }

  @Test fun `when source is not set, then throw an exception if 'setState' is called`() {
    assertThrows<IllegalStateException> {
      mviTestHelper.setState(SomeState("with another message"))
    }
  }

  @Test fun `when source is not set, then throw an exception if 'testObserver' is accessed`() {
    assertThrows<IllegalStateException> {
      mviTestHelper.testObserver
    }
  }

  @Test fun `when source is set, then don't throw an exception`() {
    mviTestHelper.setSource { _, _ ->
      Observable.never()
    }

    mviTestHelper.sourceIsCreated()
  }
}
