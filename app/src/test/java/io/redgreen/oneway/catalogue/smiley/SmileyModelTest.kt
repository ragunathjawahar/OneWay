package io.redgreen.oneway.catalogue.smiley

import io.redgreen.oneway.catalogue.smiley.usecases.SmileyUseCases
import io.redgreen.oneway.test.MviTestHelper
import org.junit.jupiter.api.Test

class SmileyModelTest {
  private val initialSmileyState = SmileyState.initial("SMILE")
  private val testHelper = MviTestHelper<SmileyState> { sourceLifecycleEvents, sourceCopy ->
    SmileyModel.createSource(sourceLifecycleEvents, SmileyUseCases(
        initialSmileyState,
        sourceCopy
    ))
  }

  @Test fun `when screen is created, then show the default (smiling) smiley`() {
    // when
    testHelper.sourceIsCreated()

    // then
    testHelper.assertStates(initialSmileyState)
  }

  @Test fun `when screen is restored, then show last known state`() {
    // given
    val grumpySmileyState = initialSmileyState
        .setSmiley("GRUMPY")
    testHelper.setState(grumpySmileyState)

    // when
    testHelper.sourceIsDestroyed()
    testHelper.sourceIsRestored()

    // then
    testHelper.assertStates(grumpySmileyState)
  }
}
